package net.createmod.ponder1710.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.DeltaTracker;

@Mixin(DeltaTracker.Timer.class)
public interface TimerAccessor {
	@Accessor("deltaTickResidual")
	float catnip$getDeltaTickResidual();
}
