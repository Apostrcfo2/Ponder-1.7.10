package net.createmod.ponder1710.api.scene;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // 1.7.10 uses ForgeDirection
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public interface VectorUtil {

    Vec3 centerOf(int x, int y, int z);

    // BlockPos -> x,y,z in 1.7.10
    // Vec3 centerOf(BlockPos pos);

    Vec3 topOf(int x, int y, int z);

    // Vec3 topOf(BlockPos pos);

    // Direction -> ForgeDirection in 1.7.10
    Vec3 blockSurface(int x, int y, int z, ForgeDirection face);

    Vec3 blockSurface(int x, int y, int z, ForgeDirection face, float margin);

    Vec3 of(double x, double y, double z);
}
