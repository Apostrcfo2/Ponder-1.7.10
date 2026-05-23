package net.createmod.ponder1710.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.outliner.Outliner;
import net.createmod.ponder1710.PonderClient;
import net.createmod.ponder1710.foundation.PonderTooltipHandler;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Hook game tick for AnimationTickHolder and Ponder
    @Inject(method = "runTick", at = @At("HEAD"))
    private void ponder$onTick(CallbackInfo ci) {
        AnimationTickHolder.tick();
        PonderClient.onTick();
        PonderTooltipHandler.tick();
    }

    // Hook deferred tooltip tick after game tick
    @Inject(method = "runTick", at = @At("TAIL"))
    private void ponder$onTickTail(CallbackInfo ci) {
        PonderTooltipHandler.deferredTick();
    }
}
