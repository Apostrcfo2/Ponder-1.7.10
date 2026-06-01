package net.createmod.ponder1710.api.scene;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

// BlockPos -> x,y,z ints in 1.7.10
// Direction -> ForgeDirection in 1.7.10
public interface VectorUtil {

    Vec3 centerOf(int x, int y, int z);

    Vec3 topOf(int x, int y, int z);

    Vec3 blockSurface(int x, int y, int z, ForgeDirection face);

    Vec3 blockSurface(int x, int y, int z, ForgeDirection face, float margin);

    Vec3 of(double x, double y, double z);
}
