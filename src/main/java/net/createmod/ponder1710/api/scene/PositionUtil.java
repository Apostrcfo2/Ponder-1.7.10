package net.createmod.ponder1710.api.scene;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z or ChunkCoordinates
// Replaced with int[] {x, y, z}

public interface PositionUtil {

    // BlockPos -> int[] {x, y, z} in 1.7.10
    int[] at(int x, int y, int z);

    int[] zero();
}
