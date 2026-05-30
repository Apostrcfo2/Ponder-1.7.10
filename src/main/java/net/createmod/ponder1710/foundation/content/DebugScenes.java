package net.createmod.ponder1710.foundation.content;

import net.createmod.metanip.math.Pointing;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.ParrotPose;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder1710.api.scene.PonderStoryBoard;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;
import net.createmod.ponder1710.api.scene.Selection;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class DebugScenes {

    private static int index;

    public static void registerAll(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        index = 1;
        add(helper, DebugScenes::coordinateScene);
        add(helper, DebugScenes::blocksScene);
        add(helper, DebugScenes::offScreenScene);
        add(helper, DebugScenes::controlsScene);
        add(helper, DebugScenes::birbScene);
        add(helper, DebugScenes::sectionsScene);
        // fluidsScene - requires fluid rendering (PORT LATER)
        // particleScene - requires ParticleTypes (PORT LATER)
    }

    private static void add(PonderSceneRegistrationHelper<ResourceLocation> helper, PonderStoryBoard sb) {
        String schematicPath = "debug/scene_" + index;
        helper.addStoryBoard(new ResourceLocation("spyglass"), schematicPath, sb).highlightAllTags();
        index++;
    }

    public static void empty(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_empty", "Missing Content");
        scene.showBasePlate();
        scene.idle(5);
    }

    public static void coordinateScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_coords", "Coordinate Space");
        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(util.select().layersFrom(1), ForgeDirection.DOWN);

        Selection xAxis = util.select().fromTo(2, 1, 1, 4, 1, 1);
        Selection yAxis = util.select().fromTo(1, 2, 1, 1, 4, 1);
        Selection zAxis = util.select().fromTo(1, 1, 2, 1, 1, 4);

        scene.idle(10);
        scene.overlay().showOutlineWithText(xAxis, 20)
            .colored(PonderPalette.RED)
            .text("Das X axis");
        scene.idle(20);
        scene.overlay().showOutlineWithText(yAxis, 20)
            .colored(PonderPalette.GREEN)
            .text("Das Y axis");
        scene.idle(20);
        scene.overlay().showOutlineWithText(zAxis, 20)
            .colored(PonderPalette.BLUE)
            .text("Das Z axis");
    }

    public static void blocksScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_blocks", "Changing Blocks");
        scene.showBasePlate();
        scene.scaleSceneView(0.75f);
        scene.idle(10);
        scene.world().showSection(util.select().layersFrom(1), ForgeDirection.DOWN);
        scene.idle(10);
        scene.overlay().showText(1000)
            .independent(10)
            .text("Blocks can be modified");
        scene.idle(20);
        // WHITE_CONCRETE -> QUARTZ_BLOCK in 1.7.10
        scene.world().replaceBlocks(util.select().fromTo(1, 1, 3, 2, 2, 4), Blocks.quartz_block, 0, true);
        scene.idle(10);
        scene.addKeyframe();
        // REDSTONE_WIRE with power 15
        scene.world().replaceBlocks(util.select().position(3, 1, 1), Blocks.redstone_wire, 15, true);
        scene.rotateCameraY(180);

        for (int i = 0; i < 20; i++) {
            scene.world().incrementBlockBreakingProgress(util.grid().at(3, 1, 1));
            scene.idle(10);
        }
        scene.markAsFinished();
    }

    public static void offScreenScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_baseplate", "Out of bounds / configureBasePlate");
        scene.configureBasePlate(1, 0, 6);
        scene.showBasePlate();

        Selection out1 = util.select().fromTo(7, 0, 0, 8, 0, 5);
        Selection out2 = util.select().fromTo(0, 0, 0, 0, 0, 5);
        Selection blocksExceptBasePlate = util.select().layersFrom(1).add(out1).add(out2);

        scene.addKeyframe();
        scene.idle(10);
        scene.world().showSection(blocksExceptBasePlate, ForgeDirection.DOWN);
        scene.idle(10);
        scene.addKeyframe();
        scene.idle(20);
        scene.addKeyframe();
        scene.idle(20);
        scene.addKeyframe();

        scene.overlay().showOutlineWithText(out1, 100)
            .colored(PonderPalette.BLACK)
            .text("Blocks outside of the base plate do not affect scaling");
        scene.overlay().showOutlineWithText(out2, 100)
            .colored(PonderPalette.BLACK)
            .text("configureBasePlate() makes sure of that.");
        scene.markAsFinished();
    }

    public static void controlsScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_controls", "Basic player interaction");
        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(util.select().layer(1), ForgeDirection.DOWN);
        scene.idle(4);
        scene.world().showSection(util.select().layer(2), ForgeDirection.DOWN);
        scene.idle(4);
        scene.world().showSection(util.select().layer(3), ForgeDirection.DOWN);
        scene.idle(10);

        int[] shaftPos = util.grid().at(3, 1, 1);
        Selection shaftSelection = util.select().position(shaftPos[0], shaftPos[1], shaftPos[2]);
        // Items.SALMON -> Items.fish in 1.7.10
        scene.overlay().showControls(util.vector().topOf(shaftPos[0], shaftPos[1], shaftPos[2]), Pointing.DOWN, 40)
            .rightClick()
            .whileSneaking()
            .withItem(new ItemStack(Items.fish));
        scene.idle(20);
        // BIRCH_SIGN -> sign in 1.7.10
        scene.world().replaceBlocks(shaftSelection, Blocks.standing_sign, 0, true);
        scene.idle(20);
        scene.world().hideSection(shaftSelection, ForgeDirection.UP);
        scene.idle(20);

        scene.overlay().showControls(util.vector().of(1, 4.5, 3.5), Pointing.LEFT, 20)
            .rightClick()
            .withItem(new ItemStack(Blocks.stone_slab2));
        scene.world().showSection(util.select().layer(4), ForgeDirection.DOWN);
        scene.idle(40);
        scene.markAsFinished();
    }

    public static void birbScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_birbs", "Birbs");
        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(util.select().layersFrom(1), ForgeDirection.DOWN);
        scene.idle(10);

        int[] pos = util.grid().at(1, 2, 3);
        scene.special().createBirb(util.vector().blockSurface(pos[0], pos[1], pos[2], ForgeDirection.UP),
            ParrotPose.FaceCursorPose::new);
        scene.overlay().showText(100)
            .colored(PonderPalette.GREEN)
            .text("More birbs = More interesting")
            .pointAt(util.vector().topOf(pos[0], pos[1], pos[2]));
        scene.idle(10);

        scene.special().createBirb(util.vector().topOf(0, 1, 2), ParrotPose.DancePose::new);
        scene.idle(10);

        scene.special().createBirb(
            util.vector().centerOf(3, 1, 3).addVector(0, 0.25, 0),
            ParrotPose.FacePointOfInterestPose::new);
        scene.idle(20);

        int[] poi1 = util.grid().at(4, 1, 0);
        int[] poi2 = util.grid().at(0, 1, 4);

        scene.world().setBlock(poi1[0], poi1[1], poi1[2], Blocks.gold_block, 0, true);
        scene.special().movePointOfInterest(poi1[0], poi1[1], poi1[2]);
        scene.idle(20);

        scene.world().setBlock(poi2[0], poi2[1], poi2[2], Blocks.gold_block, 0, true);
        scene.special().movePointOfInterest(poi2[0], poi2[1], poi2[2]);
        scene.overlay().showText(20)
            .text("Point of Interest")
            .pointAt(util.vector().centerOf(poi2[0], poi2[1], poi2[2]));
        scene.idle(20);

        scene.world().destroyBlock(poi1[0], poi1[1], poi1[2]);
        scene.special().movePointOfInterest(poi1[0], poi1[1], poi1[2]);
        scene.idle(20);

        scene.world().destroyBlock(poi2[0], poi2[1], poi2[2]);
        scene.special().movePointOfInterest(poi2[0], poi2[1], poi2[2]);
    }

    public static void sectionsScene(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("debug_sections", "Sections");
        scene.showBasePlate();
        scene.idle(10);
        scene.rotateCameraY(95);

        int[] mergePos = util.grid().at(1, 1, 1);
        int[] independentPos = util.grid().at(3, 1, 1);
        Selection toMerge = util.select().position(mergePos[0], mergePos[1], mergePos[2]);
        Selection independent = util.select().position(independentPos[0], independentPos[1], independentPos[2]);
        Selection start = util.select().layersFrom(1).substract(toMerge).substract(independent);

        scene.world().showSection(start, ForgeDirection.DOWN);
        scene.idle(20);

        scene.world().showSection(toMerge, ForgeDirection.DOWN);
        ElementLink<WorldSectionElement> link =
            scene.world().showIndependentSection(independent, ForgeDirection.DOWN);

        scene.idle(20);

        scene.overlay().showText(40)
            .colored(PonderPalette.GREEN)
            .text("This Section got merged to base.")
            .pointAt(util.vector().topOf(mergePos[0], mergePos[1], mergePos[2]));
        scene.idle(10);
        scene.overlay().showText(40)
            .colored(PonderPalette.RED)
            .text("This Section renders independently.")
            .pointAt(util.vector().topOf(independentPos[0], independentPos[1], independentPos[2]));
        scene.idle(40);

        scene.world().hideIndependentSection(link, ForgeDirection.DOWN);
        scene.world().hideSection(
            util.select().fromTo(mergePos[0], mergePos[1], mergePos[2],
                util.grid().at(1, 1, 4)[0], util.grid().at(1, 1, 4)[1], util.grid().at(1, 1, 4)[2]),
            ForgeDirection.DOWN);
        scene.idle(20);

        Selection hiddenReplaceArea = util.select().fromTo(2, 1, 2, 4, 1, 4)
            .substract(util.select().position(4, 1, 3))
            .substract(util.select().position(2, 1, 3));

        scene.world().hideSection(hiddenReplaceArea, ForgeDirection.UP);
        scene.idle(20);
        // BLACK_CONCRETE -> obsidian in 1.7.10
        scene.world().setBlocks(hiddenReplaceArea, Blocks.obsidian, 0, false);
        scene.world().showSection(hiddenReplaceArea, ForgeDirection.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(hiddenReplaceArea, 30)
            .colored(PonderPalette.BLUE)
            .text("Seamless substitution of blocks");
        scene.idle(40);

        ElementLink<WorldSectionElement> helicopter =
            scene.world().makeSectionIndependent(hiddenReplaceArea);
        scene.world().rotateSection(helicopter, 50, 5 * 360, 0, 60);
        scene.world().moveSection(helicopter, util.vector().of(0, 4, 5), 50);
        scene.overlay().showText(30)
            .colored(PonderPalette.BLUE)
            .text("Up, up and away.")
            .independent(30);
        scene.idle(40);
        scene.world().hideIndependentSection(helicopter, ForgeDirection.UP);
    }
}
