package net.createmod.ponder1710.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// In 1.7.10 partial ticks come from Minecraft.timer field (type net.minecraft.util.Timer)
// We access it via Minecraft directly instead of a Timer mixin
@Mixin(net.minecraft.util.Timer.class)
public interface TimerAccessor {
    @Accessor("elapsedPartialTicks")
    float catnip$getDeltaTickResidual();
}
