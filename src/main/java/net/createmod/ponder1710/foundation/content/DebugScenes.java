package net.createmod.ponder1710.foundation.content;

// TODO: DebugScenes uses many modern APIs not available in 1.7.10:
// - Direction (1.7.10 uses ForgeDirection)
// - BlockPos (1.7.10 uses x,y,z)
// - BlockState (not available, use Block + meta)
// - Vec3 (different package)
// - AABB (AxisAlignedBB in 1.7.10)
// - Pointing from catnip (not available)
// - ParticleTypes (not available)
// - Parrot entity (not in 1.7.10)
// - Items.SALMON, Blocks.WHITE_CONCRETE, etc. (many not in 1.7.10)
// All scenes commented out until APIs are ported

import net.createmod.ponder1710.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;
import net.minecraftforge.common.util.ForgeDirection;

// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
import net.minecraft.util.ResourceLocation;

public class DebugScenes {

    private static int index;

    public static void registerAll(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // TODO: Re-enable debug scenes once APIs are ported to 1.7.10
        // index = 1;
        // add(helper, DebugScenes::coordinateScene);
        // add(helper, DebugScenes::blocksScene);
        // ...
    }

    public static void empty(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_empty", "Missing Content");
        scene.showBasePlate();
        scene.idle(5);
    }

    // TODO: All other scene methods commented out until APIs are ported
    // public static void coordinateScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void blocksScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void fluidsScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void offScreenScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void particleScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void controlsScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void birbScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
    // public static void sectionsScene(SceneBuilder scene, SceneBuildingUtil util) { ... }
}
