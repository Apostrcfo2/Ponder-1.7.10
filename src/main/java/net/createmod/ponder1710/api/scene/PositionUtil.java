package net.createmod.ponder1710.api.scene;

// BlockPos -> int[] {x,y,z} in 1.7.10
public interface PositionUtil {

    int[] at(int x, int y, int z);

    int[] zero();
}
