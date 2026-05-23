package net.createmod.ponder1710.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.ponder1710.foundation.PonderTooltipHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {

    // Hook tooltip rendering to inject Ponder hint
    @Inject(
        method = "drawScreen",
        at = @At("TAIL")
    )
    private void ponder$onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiContainer self = (GuiContainer)(Object)this;
        ItemStack hovered = self.mc.thePlayer.inventory.getItemStack();
        if (hovered == null && self.inventorySlots != null) {
            net.minecraft.inventory.Slot slot = self.getSlotUnderMouse();
            if (slot != null && slot.getHasStack())
                hovered = slot.getStack();
        }
        PonderTooltipHandler.onHoveredItem(hovered);
    }
}
