package net.createmod.ponder1710.foundation.element;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.createmod.metanip.data.Couple;
import net.createmod.metanip.gui.element.BoxElement;
import net.createmod.metanip.theme.Color;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.TextElementBuilder;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderScene.SceneTransform;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class TextWindowElement extends AnimatedOverlayElementBase {

    public static final Couple<Color> COLOR_WINDOW_BORDER = Couple.create(
        new Color(0x607a6000, true),
        new Color(0x207a6000, true)
    ).map(Color::setImmutable);

    Supplier<String> textGetter = () -> "(?) No text was provided";
    @Nullable
    String bakedText;

    // from 0 to 200
    int y;

    @Nullable
    Vec3 vec;

    boolean nearScene = false;
    PonderPalette palette = PonderPalette.WHITE;

    public TextElementBuilder builder(PonderScene scene) {
        return new Builder(scene);
    }

    private class Builder implements TextElementBuilder {

        private final PonderScene scene;

        public Builder(PonderScene scene) {
            this.scene = scene;
        }

        @Override
        public Builder colored(PonderPalette color) {
            TextWindowElement.this.palette = color;
            return this;
        }

        @Override
        public Builder pointAt(Vec3 vec) {
            TextWindowElement.this.vec = vec;
            return this;
        }

        @Override
        public Builder independent(int y) {
            TextWindowElement.this.y = y;
            return this;
        }

        @Override
        public Builder text(String defaultText) {
            textGetter = scene.registerText(defaultText);
            return this;
        }

        @Override
        public TextElementBuilder text(String defaultText, Object... params) {
            textGetter = scene.registerText(defaultText, params);
            return this;
        }

        @Override
        public Builder sharedText(ResourceLocation key) {
            textGetter = () -> PonderIndex.getLangAccess().getShared(key);
            return this;
        }

        @Override
        public TextElementBuilder sharedText(ResourceLocation key, Object... params) {
            textGetter = () -> PonderIndex.getLangAccess().getShared(key, params);
            return this;
        }

        @Override
        public Builder sharedText(String key) {
            return sharedText(new ResourceLocation(scene.getNamespace(), key));
        }

        @Override
        public TextElementBuilder sharedText(String key, Object... params) {
            return sharedText(new ResourceLocation(scene.getNamespace(), key), params);
        }

        @Override
        public Builder placeNearTarget() {
            TextWindowElement.this.nearScene = true;
            return this;
        }

        @Override
        public Builder attachKeyFrame() {
            scene.builder().addLazyKeyframe();
            return this;
        }
    }

    @Override
    public void render(PonderScene scene, PonderUI screen, float partialTicks, float fade) {
        if (bakedText == null)
            bakedText = textGetter.get();
        if (fade < 1 / 16f) return;

        FontRenderer font = net.minecraft.client.Minecraft.getMinecraft().fontRenderer;
        SceneTransform transform = scene.getTransform();

        float[] sceneToScreen = vec != null
            ? transform.sceneToScreen(vec, partialTicks)
            : new float[]{screen.width / 2f, (screen.height - 200) / 2f + y - 8};
        float sceneScreenX = sceneToScreen[0];
        float sceneScreenY = sceneToScreen[1];

        boolean settled = transform.xRotation.settled() && transform.yRotation.settled();
        float pY = settled ? (int) sceneScreenY : sceneScreenY;

        float yDiff = (screen.height / 2f - sceneScreenY - 10) / 100f;
        float targetX = screen.width * lerp(yDiff * yDiff, 6f / 8, 5f / 8);

        if (nearScene)
            targetX = Math.min(targetX, sceneScreenX + 50);

        if (settled)
            targetX = (int) targetX;

        int textWidth = (int) Math.min(screen.width - targetX, 180);

        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>) font.listFormattedStringToWidth(bakedText, textWidth);

        int boxWidth = 0;
        for (String line : lines)
            boxWidth = Math.max(boxWidth, font.getStringWidth(line));

        int boxHeight = font.splitStringWidth(bakedText, textWidth);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, pY, 400);

        new BoxElement()
            .withBackground(PonderUI.BACKGROUND_FLAT)
            .gradientBorder(COLOR_WINDOW_BORDER)
            .at((int) targetX - 10, 3, -101)
            .withBounds(boxWidth, boxHeight - 1)
            .render();

        Color brighter = mix(palette.getColorObject(), new Color(0xff_ffffdd, true), 0.5f);
        Color c1 = new Color(0xff_494949, true);
        Color c2 = new Color(0xff_393939, true);

        if (vec != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(sceneScreenX, 0, 0);
            float lineTarget = (targetX - sceneScreenX) * fade;
            GL11.glScalef(lineTarget, 1, 1);
            drawGradientLine(0, 0, 1, 1, brighter.getRGB(), brighter.getRGB());
            drawGradientLine(0, 1, 1, 2, c1.getRGB(), c2.getRGB());
            GL11.glPopMatrix();
        }

        GL11.glTranslatef(0, 0, 400);
        int textColor = brighter.scaleAlpha(fade).getRGB();
        for (int i = 0; i < lines.size(); i++)
            font.drawString(lines.get(i), (int) targetX - 10, 3 + 9 * i, textColor);

        GL11.glPopMatrix();
    }

    private static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    private static Color mix(Color a, Color b, float t) {
        int ar = (a.getRGB() >> 16) & 0xFF, ag = (a.getRGB() >> 8) & 0xFF, ab = a.getRGB() & 0xFF;
        int br = (b.getRGB() >> 16) & 0xFF, bg = (b.getRGB() >> 8) & 0xFF, bb = b.getRGB() & 0xFF;
        int r = (int) (ar + (br - ar) * t);
        int g = (int) (ag + (bg - ag) * t);
        int bl = (int) (ab + (bb - ab) * t);
        return new Color(0xFF000000 | (r << 16) | (g << 8) | bl, true);
    }

    private static void drawGradientLine(float x1, float y1, float x2, float y2, int col1, int col2) {
        float a1 = ((col1 >> 24) & 0xFF) / 255f, r1 = ((col1 >> 16) & 0xFF) / 255f, g1 = ((col1 >> 8) & 0xFF) / 255f, b1 = (col1 & 0xFF) / 255f;
        float a2 = ((col2 >> 24) & 0xFF) / 255f, r2 = ((col2 >> 16) & 0xFF) / 255f, g2 = ((col2 >> 8) & 0xFF) / 255f, b2 = (col2 & 0xFF) / 255f;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(r1, g1, b1, a1); tess.addVertex(x1, y1, -100);
        tess.setColorRGBA_F(r1, g1, b1, a1); tess.addVertex(x1, y2, -100);
        tess.setColorRGBA_F(r2, g2, b2, a2); tess.addVertex(x2, y2, -100);
        tess.setColorRGBA_F(r2, g2, b2, a2); tess.addVertex(x2, y1, -100);
        tess.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public PonderPalette getPalette() {
        return palette;
    }
}
