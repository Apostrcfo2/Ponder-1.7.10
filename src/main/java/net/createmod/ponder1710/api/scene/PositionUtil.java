package net.createmod.ponder1710.api.scene;

import net.minecraft.core.BlockPos;

public interface PositionUtil {
	BlockPos at(int x, int y, int z);

	BlockPos zero();
}
