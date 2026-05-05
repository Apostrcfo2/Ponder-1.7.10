package net.createmod.ponder1710.foundation.element;

import java.util.function.Supplier;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.catnip.data.Couple; // TODO: catnip not available
// import net.createmod.catnip.gui.element.BoxElement; // TODO: catnip not available
// import net.createmod.catnip.theme.Color; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.network.chat.FormattedText; // not available in 1.7.10
// import net.minecraft.network.chat.Style; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.phys.Vec2; // not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

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
    // TODO: render - GuiGraphics, BoxElement, Color from catnip not available in 1.7.10
    // Full reimplementation needed using GL11 and FontRenderer
    public void render(PonderScene scene, PonderUI screen, float partialTicks, float fade) {
        if (bakedText == null)
            bakedText = textGetter.get();

        if (fade < 1 / 16f)
            return;

        // TODO: sceneToScreen not available yet - needs SceneTransform port
        // TODO: BoxElement from catnip not available
        // TODO: Color from catnip not available
        // TODO: FormattedText/Style not available in 1.7.10
        // Full rendering to be reimplemented using GL11 + FontRenderer
    }

    public PonderPalette getPalette() {
        return palette;
    }
}
