package net.createmod.metanip.math;

import net.minecraftforge.common.util.ForgeDirection;

public class DirectionHelper {

    public static ForgeDirection rotateAround(ForgeDirection dir, int axis) {
        // axis: 0=X, 1=Y, 2=Z
        switch (axis) {
            case 0: // X
                if (dir != ForgeDirection.WEST && dir != ForgeDirection.EAST)
                    return rotateX(dir);
                return dir;
            case 1: // Y
                if (dir != ForgeDirection.UP && dir != ForgeDirection.DOWN)
                    return rotateClockWise(dir);
                return dir;
            case 2: // Z
                if (dir != ForgeDirection.NORTH && dir != ForgeDirection.SOUTH)
                    return rotateZ(dir);
                return dir;
            default:
                throw new IllegalStateException("Invalid axis: " + axis);
        }
    }

    public static ForgeDirection rotateX(ForgeDirection dir) {
        switch (dir) {
            case NORTH: return ForgeDirection.DOWN;
            case SOUTH: return ForgeDirection.UP;
            case UP:    return ForgeDirection.NORTH;
            case DOWN:  return ForgeDirection.SOUTH;
            default: throw new IllegalStateException("Unable to get X-rotated facing of " + dir);
        }
    }

    public static ForgeDirection rotateZ(ForgeDirection dir) {
        switch (dir) {
            case EAST:  return ForgeDirection.DOWN;
            case WEST:  return ForgeDirection.UP;
            case UP:    return ForgeDirection.EAST;
            case DOWN:  return ForgeDirection.WEST;
            default: throw new IllegalStateException("Unable to get Z-rotated facing of " + dir);
        }
    }

    public static ForgeDirection rotateClockWise(ForgeDirection dir) {
        switch (dir) {
            case NORTH: return ForgeDirection.EAST;
            case EAST:  return ForgeDirection.SOUTH;
            case SOUTH: return ForgeDirection.WEST;
            case WEST:  return ForgeDirection.NORTH;
            default:    return dir;
        }
    }

    public static ForgeDirection getPositivePerpendicular(int horizontalAxis) {
        return horizontalAxis == 0 ? ForgeDirection.SOUTH : ForgeDirection.EAST;
    }
}
