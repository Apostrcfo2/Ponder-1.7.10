package net.createmod.ponder1710.foundation.content;

import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;

// import net.minecraft.core.Direction; // 1.7.10 uses ForgeDirection
import net.minecraftforge.common.util.ForgeDirection;

public class TemplateScenes {

    public static void templateMethod(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("", "");
        scene.configureBasePlate(0, 0, 5);
        // Direction.UP -> ForgeDirection.UP in 1.7.10
        scene.world().showSection(util.select().layer(0), ForgeDirection.UP);
        scene.idle(5);
        scene.world().showSection(util.select().layersFrom(1), ForgeDirection.DOWN);
    }
}
