package net.createmod.ponder1710.foundation.content;

import java.util.function.Consumer;

import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class StoneScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(
            new ResourceLocation("stone"),
            (Consumer<PonderLevel>) StoneScenes::setupWorld,
            StoneScenes::whatIsStone
        ).highlightAllTags();
    }

    public static void setupWorld(PonderLevel world) {
        // Floor for the base plate (5x5), one layer below the stone block
        for (int x = 0; x < 5; x++)
            for (int z = 0; z < 5; z++)
                world.setBlock(x, 0, z, Blocks.stonebrick, 0, 3);

        world.setBlock(2, 1, 2, Blocks.stone, 0, 3);

        // Without this, world.getBounds() stays {0,0,0,0,0,0}, which makes every
        // SceneBuildingUtil helper (layersFrom, layer, fullSpace, etc.) compute a
        // degenerate 1x1x1 selection instead of covering the real scene volume.
        world.setBoundaries(0, 0, 0, 4, 1, 4);

        world.createBackup();
    }

    public static void whatIsStone(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("stone_intro", "Stone");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        // Show all blocks from layer 1 up (base plate is layer 0)
        scene.world().showSection(util.select().layersFrom(1), ForgeDirection.DOWN);
        scene.idle(20);

        scene.overlay().showText(60)
            .text("This is a Stone block.")
            .pointAt(util.vector().blockSurface(2, 1, 2, ForgeDirection.UP))
            .colored(PonderPalette.WHITE)
            .attachKeyFrame();

        scene.idle(70);
        scene.markAsFinished();
    }
}
