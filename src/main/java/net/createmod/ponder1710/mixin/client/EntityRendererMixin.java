package net.createmod.ponder1710.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.ponder1710.PonderClient;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    // Hook after world render for outlines and ghost blocks
    @Inject(
        method = "renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            shift = At.Shift.AFTER
        )
    )
    private void ponder$onRenderWorld(float partialTicks, long timeSlice, CallbackInfo ci) {
        PonderClient.onRenderWorld();
    }
}
