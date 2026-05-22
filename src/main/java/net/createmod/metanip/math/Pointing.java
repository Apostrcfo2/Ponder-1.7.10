package net.createmod.metanip.math;

import net.createmod.metanip.lang.Lang;
import net.minecraftforge.common.util.ForgeDirection;

public enum Pointing {
    UP(0), LEFT(270), DOWN(180), RIGHT(90);

    private final int xRotation;

    Pointing(int xRotation) { this.xRotation = xRotation; }

    public String getSerializedName() { return Lang.asId(name()); }

    public int getXRotation() { return xRotation; }

    public ForgeDirection getCombinedDirection(ForgeDirection direction) {
        // Replicate clockwise rotation logic for 1.7.10 using ForgeDirection
        ForgeDirection top = (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
            ? ForgeDirection.SOUTH : ForgeDirection.UP;

        int rotations = direction.offsetY < 0 ? 4 - ordinal() : ordinal();
        for (int i = 0; i < rotations; i++)
            top = rotateClockWise(top, direction);
        return top;
    }

    private static ForgeDirection rotateClockWise(ForgeDirection dir, ForgeDirection axis) {
        // Rotate dir clockwise around axis
        if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN) {
            switch (dir) {
                case NORTH: return ForgeDirection.EAST;
                case EAST:  return ForgeDirection.SOUTH;
                case SOUTH: return ForgeDirection.WEST;
                case WEST:  return ForgeDirection.NORTH;
                default:    return dir;
            }
        } else if (axis == ForgeDirection.EAST || axis == ForgeDirection.WEST) {
            switch (dir) {
                case UP:    return ForgeDirection.SOUTH;
                case SOUTH: return ForgeDirection.DOWN;
                case DOWN:  return ForgeDirection.NORTH;
                case NORTH: return ForgeDirection.UP;
                default:    return dir;
            }
        } else {
            switch (dir) {
                case UP:   return ForgeDirection.EAST;
                case EAST: return ForgeDirection.DOWN;
                case DOWN: return ForgeDirection.WEST;
                case WEST: return ForgeDirection.UP;
                default:   return dir;
            }
        }
    }
}
