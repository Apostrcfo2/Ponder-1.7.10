package net.createmod.ponder1710.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.ponder1710.PonderClient;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "func_78480_b", at = @At("TAIL"))
    private void ponder$onRenderWorld(float partialTicks, CallbackInfo ci) {
        PonderClient.onRenderWorld();
    }
}
