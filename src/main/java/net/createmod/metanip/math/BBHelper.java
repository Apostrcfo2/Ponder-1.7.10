package net.createmod.metanip.math;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.world.level.levelgen.structure.BoundingBox; // not available in 1.7.10
// Using int[] {minX, minY, minZ, maxX, maxY, maxZ} as BoundingBox replacement

public class BBHelper {

    public static int[] encapsulate(int[] bb, int x, int y, int z) {
        return new int[]{
            Math.min(bb[0], x), Math.min(bb[1], y), Math.min(bb[2], z),
            Math.max(bb[3], x), Math.max(bb[4], y), Math.max(bb[5], z)
        };
    }

    public static int[] encapsulate(int[] bb, int[] bb2) {
        return new int[]{
            Math.min(bb[0], bb2[0]), Math.min(bb[1], bb2[1]), Math.min(bb[2], bb2[2]),
            Math.max(bb[3], bb2[3]), Math.max(bb[4], bb2[4]), Math.max(bb[5], bb2[5])
        };
    }
}
