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

    // Track hovered item each frame so PonderTooltipHandler can update
    @Inject(method = "func_73863_a", at = @At("TAIL"))
    private void ponder$onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiContainer self = (GuiContainer)(Object)this;

        ItemStack hovered = null;
        net.minecraft.inventory.Slot slot = null; // TODO: get hovered slot via mixin accessor
        if (slot != null && slot.getHasStack())
            hovered = slot.getStack();

        if (hovered != null)
            if (hovered != null) net.createmod.ponder1710.foundation.PonderTooltipHandler.setHovered(hovered);
    }
}
