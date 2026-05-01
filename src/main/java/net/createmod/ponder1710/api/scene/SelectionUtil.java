package net.createmod.ponder1710.api.scene;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Vec3i; // not available in 1.7.10

public interface SelectionUtil {

    Selection everywhere();

    Selection position(int x, int y, int z);

    // Selection position(BlockPos pos); // BlockPos -> x,y,z in 1.7.10

    Selection fromTo(int x, int y, int z, int x2, int y2, int z2);

    // Selection fromTo(BlockPos pos1, BlockPos pos2);

    Selection column(int x, int z);

    Selection layer(int y);

    Selection layersFrom(int y);

    Selection layers(int y, int height);

    // Selection cuboid(BlockPos origin, Vec3i size); // replaced below
    Selection cuboid(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ);
}
