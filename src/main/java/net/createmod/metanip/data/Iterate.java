package net.createmod.metanip.data;

import java.util.Arrays;
import java.util.List;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.core.Direction.Axis; // not available in 1.7.10

import net.minecraftforge.common.util.ForgeDirection;

public class Iterate {

    public static final boolean[] trueAndFalse = {true, false};
    public static final boolean[] falseAndTrue = {false, true};
    public static final int[] zeroAndOne = {0, 1};
    public static final int[] positiveAndNegative = {1, -1};

    // ForgeDirection.values() in 1.7.10
    public static final ForgeDirection[] directions = ForgeDirection.values();
    public static final ForgeDirection[] horizontalDirections = {
        ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST
    };

    public static ForgeDirection[] directionsInAxis(int axis) {
        switch (axis) {
            case 0: // X
                return new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST};
            case 1: // Y
                return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN};
            default: // Z
                return new ForgeDirection[]{ForgeDirection.SOUTH, ForgeDirection.NORTH};
        }
    }

    // BlockPos.below/above -> use y-1, y+1 in 1.7.10
    public static List<int[]> hereAndBelow(int x, int y, int z) {
        return Arrays.asList(new int[]{x, y, z}, new int[]{x, y-1, z});
    }

    public static List<int[]> hereBelowAndAbove(int x, int y, int z) {
        return Arrays.asList(new int[]{x, y, z}, new int[]{x, y-1, z}, new int[]{x, y+1, z});
    }

    public static <T> T cycleValue(List<T> list, T current) {
        int currentIndex = list.indexOf(current);
        if (currentIndex == -1)
            throw new IllegalArgumentException("Current value not found in list");
        return list.get((currentIndex + 1) % list.size());
    }
}
