package net.createmod.ponder1710.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Timer;

// Replaces DeltaTracker.Timer - in 1.7.10 Timer has elapsedPartialTicks field
@Mixin(Timer.class)
public interface TimerAccessor {
    @Accessor("elapsedPartialTicks")
    float catnip$getDeltaTickResidual();
}
