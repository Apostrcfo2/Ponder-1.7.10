package net.createmod.metanip.levelWrappers;

// import net.minecraft.core.registries.Registries; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.level.LevelAccessor; // not available in 1.7.10

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class WorldHelper {

    // In 1.7.10 dimension ID is an int (0=overworld, -1=nether, 1=end)
    public static int getDimensionID(World world) {
        return world.provider.dimensionId;
    }

    // Helper to get a ResourceLocation-style dimension key
    public static ResourceLocation getDimensionKey(World world) {
        int dim = world.provider.dimensionId;
        switch (dim) {
            case -1: return new ResourceLocation("minecraft", "the_nether");
            case  1: return new ResourceLocation("minecraft", "the_end");
            default: return new ResourceLocation("minecraft", "overworld");
        }
    }
}
