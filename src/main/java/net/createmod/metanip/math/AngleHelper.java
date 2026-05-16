package net.createmod.metanip.math;

// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.core.Direction.Axis; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10

import net.minecraftforge.common.util.ForgeDirection;

public class AngleHelper {

    public static float horizontalAngle(ForgeDirection facing) {
        if (facing == ForgeDirection.UP || facing == ForgeDirection.DOWN)
            return 0;
        switch (facing) {
            case SOUTH: return 0;
            case WEST:  return 90;
            case NORTH: return 180;
            case EAST:  return 270;
            default:    return 0;
        }
    }

    public static float verticalAngle(ForgeDirection facing) {
        return facing == ForgeDirection.UP ? -90 : facing == ForgeDirection.DOWN ? 90 : 0;
    }

    public static float rad(double angle) {
        if (angle == 0) return 0;
        return (float) (angle / 180 * Math.PI);
    }

    public static float deg(double angle) {
        if (angle == 0) return 0;
        return (float) (angle * 180 / Math.PI);
    }

    public static float angleLerp(double pct, double current, double target) {
        return (float) (current + getShortestAngleDiff(current, target) * pct);
    }

    public static float getShortestAngleDiff(double current, double target) {
        current = current % 360;
        target = target % 360;
        return (float) (((((target - current) % 360) + 540) % 360) - 180);
    }

    public static float getShortestAngleDiff(double current, double target, float hint) {
        float diff = getShortestAngleDiff(current, target);
        if (Math.abs(Math.abs(diff) - 180) < 1e-5 && Math.signum(diff) != Math.signum(hint))
            return diff + 360 * Math.signum(hint);
        return diff;
    }

    public static float wrapAngle180(float angle) {
        return (angle + 180) % 360 - 180;
    }
}
