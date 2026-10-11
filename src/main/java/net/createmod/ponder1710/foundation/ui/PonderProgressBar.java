package net.createmod.ponder1710.foundation.ui;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.animation.LerpedFloat.Chaser;
import net.createmod.metanip.data.Couple;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.BoxElement;
import net.createmod.metanip.theme.Color;
import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class PonderProgressBar extends GuiButton {

    public static final Couple<Color> BAR_COLORS = Couple.create(
        new Color(0x80_aaaadd, true),
        new Color(0x50_aaaadd, true)
    ).map(Color::setImmutable);

    LerpedFloat progress = LerpedFloat.linear().startWithValue(0);
    PonderUI ponder;

    public PonderProgressBar(PonderUI ponder, int x, int y, int w, int h) {
        super(0, x, y, w, h, "");
        this.ponder = ponder;
    }

    public void tick() {
        progress.chase(ponder.getActiveScene().getSceneProgress(), .5f, Chaser.EXP);
        progress.tickChaser();
    }

    protected boolean isClickable(int mouseX, int mouseY) {
        return enabled && visible
            && ponder.getActiveScene().getKeyframeCount() > 0
            && mouseX >= xPosition && mouseX < xPosition + width + 4
            && mouseY >= yPosition - 3 && mouseY < yPosition + height + 20;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        // Use field_146123_n (set by drawButton every frame with confirmed-correct coords)
        // instead of isClickable() which may receive coords from a different source
        if (!enabled || !visible || !field_146123_n) return false;
        PonderScene scene = ponder.getActiveScene();
        int idx = getHoveredKeyframeIndex(scene, mouseX);
        if (idx == -1)          ponder.seekToTime(0);
        else if (idx == scene.getKeyframeCount()) ponder.seekToTime(scene.getTotalTime());
        else                    ponder.seekToTime(scene.getKeyframeTime(idx));
        return true;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        field_146123_n = isClickable(mouseX, mouseY);
        doRender(mouseX, mouseY, 0);
    }

    public void doRender(int mouseX, int mouseY, float pt) {
        float prog = progress.getValue(pt);
        PonderScene scene = ponder.getActiveScene();
        Tessellator tess = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Background bar (full width, dim)
        Color bg = PonderUI.COLOR_IDLE.getFirst();
        GL11.glColor4f(bg.getRed()/255f, bg.getGreen()/255f, bg.getBlue()/255f, bg.getAlpha()/255f);
        tess.startDrawingQuads();
        tess.addVertex(xPosition,         yPosition + height, 400);
        tess.addVertex(xPosition + width, yPosition + height, 400);
        tess.addVertex(xPosition + width, yPosition,          400);
        tess.addVertex(xPosition,         yPosition,          400);
        tess.draw();

        // Progress fill (scales with progress)
        int fillW = Math.max(0, (int)((width + 4) * prog));
        Color c1 = BAR_COLORS.getFirst();
        Color c2 = BAR_COLORS.getSecond();
        if (fillW > 0) {
            // Top half (brighter)
            GL11.glColor4f(c1.getRed()/255f, c1.getGreen()/255f, c1.getBlue()/255f, c1.getAlpha()/255f);
            tess.startDrawingQuads();
            tess.addVertex(xPosition - 2,          yPosition,     410);
            tess.addVertex(xPosition - 2 + fillW,  yPosition,     410);
            tess.addVertex(xPosition - 2 + fillW,  yPosition + 1, 410);
            tess.addVertex(xPosition - 2,          yPosition + 1, 410);
            tess.draw();
            // Bottom half (dimmer)
            GL11.glColor4f(c2.getRed()/255f, c2.getGreen()/255f, c2.getBlue()/255f, c2.getAlpha()/255f);
            tess.startDrawingQuads();
            tess.addVertex(xPosition - 2,          yPosition + 1,          410);
            tess.addVertex(xPosition - 2 + fillW,  yPosition + 1,          410);
            tess.addVertex(xPosition - 2 + fillW,  yPosition + height + 2, 410);
            tess.addVertex(xPosition - 2,          yPosition + height + 2, 410);
            tess.draw();
        }

        // Keyframe markers
        int totalTime = scene.getTotalTime();
        if (totalTime > 0) {
            int hoverIdx = field_146123_n ? getHoveredKeyframeIndex(scene, mouseX) : -2;
            Color hover = PonderUI.COLOR_HOVER.getFirst().copy().setAlpha(0xe0);
            Color idle  = PonderUI.COLOR_HOVER.getFirst().copy().setAlpha(0x70);

            for (int i = 0; i < scene.getKeyframeCount(); i++) {
                int kTime = scene.getKeyframeTime(i);
                int kX = xPosition - 2 + (int)(((float) kTime) / totalTime * (width + 2));
                boolean sel = i == hoverIdx;
                Color c = sel ? hover : idle;
                int kh = sel ? 8 : 4;
                GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
                tess.startDrawingQuads();
                tess.addVertex(kX,     yPosition + height + kh, 420);
                tess.addVertex(kX + 2, yPosition + height + kh, 420);
                tess.addVertex(kX + 2, yPosition,               420);
                tess.addVertex(kX,     yPosition,               420);
                tess.draw();
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // BoxElement border on top
        new BoxElement()
            .withBackground(new Color(0x00_000000, true))
            .gradientBorder(PonderUI.COLOR_IDLE)
            .at(xPosition - 2, yPosition - 2, 390)
            .withBounds(width + 4, height + 4)
            .render();

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public int getHoveredKeyframeIndex(PonderScene scene, double mouseX) {
        int totalTime = scene.getTotalTime();
        if (totalTime == 0) return -1;
        int clickedAtTime = (int)((mouseX - xPosition + 2) / ((double) width + 4) * totalTime);

        if (scene.getKeyframeCount() > 0) {
            int lastKTime = scene.getKeyframeTime(scene.getKeyframeCount() - 1);
            int diffToEnd  = totalTime - clickedAtTime;
            int diffToLast = clickedAtTime - lastKTime;
            if (diffToEnd > 0 && diffToEnd < diffToLast / 2)
                return scene.getKeyframeCount();
        }

        int index = -1;
        for (int i = 0; i < scene.getKeyframeCount(); i++) {
            if (scene.getKeyframeTime(i) > clickedAtTime) break;
            index = i;
        }
        return index;
    }
}
