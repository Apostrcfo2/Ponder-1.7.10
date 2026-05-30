package net.createmod.ponder1710.foundation.element;

import java.util.function.Supplier;

import javax.annotation.Nullable;


import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.BoxElement;
import net.createmod.metanip.theme.Color;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.TextElementBuilder;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderScene.SceneTransform;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class TextWindowElement extends AnimatedOverlayElementBase {

    Supplier<String> textGetter = () -> "(?) No text was provided";
    @Nullable
    String bakedText;

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
            // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
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

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        int screenW = screen.width;
        int screenH = screen.height;

        int textX, textY;

        if (vec != null) {
            // Project 3D scene coordinate to 2D screen
            Vec3 screenPos = scene.getTransform().screenToScene(screenW / 2.0, screenH / 2.0, 100, partialTicks);
            // Approximate: use scene transform to get screen coords
            textX = (int)(screenW * 0.5 + (vec.xCoord - screenPos.xCoord) * 30);
            textY = (int)(screenH * 0.4 + (vec.yCoord - screenPos.yCoord) * 30);
        } else {
            textX = screenW / 2;
            textY = screenH / 2 + y;
        }

        int color = palette.getColor().getRGB();
        int textWidth = mc.fontRendererObj.getStringWidth(bakedText);
        int boxW = textWidth + 12;
        int boxH = 16;

        // Background box
        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glTranslatef(0, 0, 200);
        new BoxElement()
            .withBackground(new Color(0xdd000000, true))
            .gradientBorder(net.createmod.metanip.data.Couple.create(
                new Color(color, true).scaleAlpha(fade),
                new Color(color, true).scaleAlpha(fade * 0.5f)
            ))
            .at(textX - boxW/2, textY - 2, 0)
            .withBounds(boxW, boxH)
            .render();

        org.lwjgl.opengl.GL11.glTranslatef(0, 0, 10);
        int textColor = new Color(color, true).scaleAlpha(fade).getRGB();
        mc.fontRendererObj.drawStringWithShadow(bakedText, textX - textWidth/2, textY + 2, textColor);
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    public PonderPalette getPalette() {
        return palette;
    }
}
