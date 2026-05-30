package net.createmod.ponder1710.foundation.element;

import javax.annotation.Nullable;


import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.metanip.math.Pointing;
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
    public void render(PonderScene scene, PonderUI screen, float partialTicks, float fade) {
        if (fade < 1/16f) return;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();

        // Project scene coordinate to screen
        Vec3 screenPos = scene.getTransform().screenToScene(
            screen.width / 2.0, screen.height / 2.0, 100, partialTicks);
        int sx = (int)(screen.width * 0.5 + (sceneSpace.xCoord - screenPos.xCoord) * 30);
        int sy = (int)(screen.height * 0.4 + (sceneSpace.yCoord - screenPos.yCoord) * 30);

        // Determine pointing direction
        Pointing pointing = Pointing.DOWN;
        if (direction == ForgeDirection.UP)    pointing = Pointing.DOWN;
        if (direction == ForgeDirection.DOWN)  pointing = Pointing.UP;
        if (direction == ForgeDirection.WEST)  pointing = Pointing.RIGHT;
        if (direction == ForgeDirection.EAST)  pointing = Pointing.LEFT;
        if (direction == ForgeDirection.NORTH) pointing = Pointing.DOWN;
        if (direction == ForgeDirection.SOUTH) pointing = Pointing.UP;

        int boxW = 30, boxH = 24;

        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glTranslatef(sx, sy, 200);

        // Key label
        if (key != null) {
            String label = net.createmod.ponder1710.foundation.PonderIndex.getLangAccess()
                .getShared(key);
            boxW = Math.max(boxW, mc.fontRendererObj.getStringWidth(label) + 10);
            PonderUI.renderSpeechBox(0, 0, boxW, boxH, false, pointing, true);
            mc.fontRendererObj.drawStringWithShadow(label,
                -mc.fontRendererObj.getStringWidth(label)/2, 8, 0xFFFFFF);
        }

        // Icon
        if (icon != null) {
            icon.render(-8, -8);
        }

        // Item
        if (item != null) {
            GuiGameElement.of(item).at(-8, -8).render();
        }

        org.lwjgl.opengl.GL11.glPopMatrix();
    }
}
