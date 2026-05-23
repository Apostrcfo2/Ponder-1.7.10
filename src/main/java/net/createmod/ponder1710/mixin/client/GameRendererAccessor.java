package net.createmod.ponder1710.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.EntityRenderer;

// Replaces GameRendererAccessor - in 1.7.10 FOV is in EntityRenderer
@Mixin(EntityRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFOVModifier")
    double catnip$callGetFov(float partialTicks, boolean useFOVSetting);
}
