package net.createmod.ponder1710.api.scene;

// BlockPos -> x,y,z ints in 1.7.10
// Vec3i -> sizeX,sizeY,sizeZ ints in 1.7.10
public interface SelectionUtil {

    Selection everywhere();

    Selection position(int x, int y, int z);

    Selection fromTo(int x, int y, int z, int x2, int y2, int z2);

    Selection column(int x, int z);

    Selection layer(int y);

    Selection layersFrom(int y);

    Selection layers(int y, int height);

    Selection cuboid(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ);
}
