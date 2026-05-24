package net.createmod.ponder1710.foundation.element;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import net.createmod.metanip.gui.element.GuiGameElement; // TODO: catnip not available
// import net.createmod.metanip.gui.element.ScreenElement; // TODO: catnip not available
// import net.createmod.metanip.math.Pointing; // TODO: catnip not available
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.phys.Vec2; // not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.InputElementBuilder;
import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class InputWindowElement extends AnimatedOverlayElementBase {

    private final Vec3 sceneSpace;
    // TODO: Pointing from catnip not available - replaced with ForgeDirection
    private final ForgeDirection direction;
    @Nullable
    ResourceLocation key;
    // TODO: ScreenElement from catnip not available
    // @Nullable ScreenElement icon;
    @Nullable
    PonderGuiTextures icon;
    ItemStack item = null;

    public InputWindowElement(Vec3 sceneSpace, ForgeDirection direction) {
        this.sceneSpace = sceneSpace;
        this.direction = direction;
    }

    public InputElementBuilder builder() {
        return new Builder();
    }

    private class Builder implements InputElementBuilder {

        @Override
        public InputElementBuilder withItem(ItemStack stack) {
            item = stack;
            return this;
        }

        @Override
        public InputElementBuilder leftClick() {
            icon = PonderGuiTextures.ICON_LMB;
            return this;
        }

        @Override
        public InputElementBuilder scroll() {
            icon = PonderGuiTextures.ICON_SCROLL;
            return this;
        }

        @Override
        public InputElementBuilder rightClick() {
            icon = PonderGuiTextures.ICON_RMB;
            return this;
        }

        @Override
        public InputElementBuilder whileSneaking() {
            key = Ponder.asResource("sneak_and");
            return this;
        }

        @Override
        public InputElementBuilder whileCTRL() {
            key = Ponder.asResource("ctrl_and");
            return this;
        }
    }

    @Override
    // TODO: render(PonderScene, PonderUI, GuiGraphics, float, float)
    // GuiGraphics not available in 1.7.10 - render will use GL11 directly
    public void render(PonderScene scene, PonderUI screen, float partialTicks, float fade) {
        // TODO: Reimplement using GL11 and FontRenderer
        // Original used GuiGraphics.drawString and PonderUI.renderSpeechBox
    }
}
