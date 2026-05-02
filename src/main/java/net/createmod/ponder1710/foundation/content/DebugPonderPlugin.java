package net.createmod.ponder1710.foundation.content;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.registration.PonderPlugin;
import net.createmod.ponder1710.api.registration.PonderSceneRegistrationHelper;

// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
import net.minecraft.util.ResourceLocation;

public class DebugPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return Ponder.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        DebugScenes.registerAll(helper);
    }
}
