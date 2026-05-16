package net.createmod.metanip.math;

// import net.createmod.metanip.lang.Lang; // TODO: port later
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.util.StringRepresentable; // not available in 1.7.10

import net.minecraftforge.common.util.ForgeDirection;

public enum Pointing {
    UP(0), LEFT(270), DOWN(180), RIGHT(90);

    private final int xRotation;

    Pointing(int xRotation) {
        this.xRotation = xRotation;
    }

    public String getSerializedName() {
        return name().toLowerCase();
    }

    public int getXRotation() {
        return xRotation;
    }

    public ForgeDirection getCombinedDirection(ForgeDirection direction) {
        // TODO: clockwise rotation logic - simplified for 1.7.10
        return direction;
    }
}
