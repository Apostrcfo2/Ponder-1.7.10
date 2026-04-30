package net.createmod.ponder1710.api.registration;

import net.createmod.ponder1710.api.level.PonderLevel;

// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different package
import net.minecraft.util.ResourceLocation;

public interface PonderPlugin {

    String getModId();

    default void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
    }

    default void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
    }

    default void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    default void onPonderLevelRestore(PonderLevel ponderLevel) {
    }

    default void indexExclusions(IndexExclusionHelper helper) {
    }
}
