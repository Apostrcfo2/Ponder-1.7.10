package net.createmod.ponder1710.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.createmod.ponder1710.foundation.PonderTooltipHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(
        method = "getTooltip",
        at = @At("RETURN")
    )
    private void ponder$addTooltip(EntityPlayer player, boolean advanced,
                                    CallbackInfoReturnable<List> cir) {
        List tooltip = cir.getReturnValue();
        ItemStack self = (ItemStack)(Object)this;
        tooltip.addAll(PonderTooltipHandler.addToTooltip(self));
    }
}
