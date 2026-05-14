package net.createmod.ponder1710.foundation;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

// import org.joml.Vector3f; // not available in 1.7.10
// import net.createmod.catnip.math.Pointing; // TODO: catnip not available
// import net.createmod.catnip.math.VecHelper; // TODO: catnip not available
// import net.createmod.catnip.theme.Color; // TODO: catnip not available
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.core.Direction.Axis; // ForgeDirection in 1.7.10
// import net.minecraft.core.HolderLookup; // not available in 1.7.10
// import net.minecraft.core.Vec3i; // not available in 1.7.10
// import net.minecraft.core.particles.DustParticleOptions; // not available in 1.7.10
// import net.minecraft.core.particles.ParticleOptions; // not available in 1.7.10
// import net.minecraft.nbt.CompoundTag; // NBTTagCompound in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.entity.Entity; // different package in 1.7.10
// import net.minecraft.world.entity.item.ItemEntity; // different in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.level.Level; // World in 1.7.10
// import net.minecraft.world.level.block.Blocks; // different in 1.7.10
// import net.minecraft.world.level.block.RedstoneTorchBlock; // different in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntity; // TileEntity in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // not available in 1.7.10
// import net.minecraft.world.level.block.state.properties.BlockStateProperties; // not available
// import net.minecraft.world.level.block.state.properties.Property; // not available
// import net.minecraft.world.phys.AABB; // AxisAlignedBB in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.ParticleEmitter;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.EntityElement;
import net.createmod.ponder1710.api.element.InputElementBuilder;
import net.createmod.ponder1710.api.element.MinecartElement;
import net.createmod.ponder1710.api.element.MinecartElement.MinecartConstructor;
import net.createmod.ponder1710.api.element.ParrotElement;
import net.createmod.ponder1710.api.element.ParrotPose;
import net.createmod.ponder1710.api.element.TextElementBuilder;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.DebugInstructions;
import net.createmod.ponder1710.api.scene.EffectInstructions;
import net.createmod.ponder1710.api.scene.OverlayInstructions;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.api.scene.SpecialInstructions;
import net.createmod.ponder1710.api.scene.WorldInstructions;
import net.createmod.ponder1710.foundation.element.ElementLinkImpl;
import net.createmod.ponder1710.foundation.element.EntityElementImpl;
import net.createmod.ponder1710.foundation.element.InputWindowElement;
import net.createmod.ponder1710.foundation.element.MinecartElementImpl;
import net.createmod.ponder1710.foundation.element.ParrotElementImpl;
import net.createmod.ponder1710.foundation.element.TextWindowElement;
import net.createmod.ponder1710.foundation.element.WorldSectionElementImpl;
import net.createmod.ponder1710.foundation.instruction.AnimateMinecartInstruction;
import net.createmod.ponder1710.foundation.instruction.AnimateParrotInstruction;
import net.createmod.ponder1710.foundation.instruction.AnimateWorldSectionInstruction;
import net.createmod.ponder1710.foundation.instruction.BlockEntityDataInstruction;
import net.createmod.ponder1710.foundation.instruction.ChaseAABBInstruction;
import net.createmod.ponder1710.foundation.instruction.CreateMinecartInstruction;
import net.createmod.ponder1710.foundation.instruction.CreateParrotInstruction;
import net.createmod.ponder1710.foundation.instruction.DelayInstruction;
import net.createmod.ponder1710.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder1710.foundation.instruction.EmitParticlesInstruction;
import net.createmod.ponder1710.foundation.instruction.FadeOutOfSceneInstruction;
import net.createmod.ponder1710.foundation.instruction.HighlightValueBoxInstruction;
import net.createmod.ponder1710.foundation.instruction.KeyframeInstruction;
import net.createmod.ponder1710.foundation.instruction.LineInstruction;
import net.createmod.ponder1710.foundation.instruction.MarkAsFinishedInstruction;
import net.createmod.ponder1710.foundation.instruction.MovePoiInstruction;
import net.createmod.ponder1710.foundation.instruction.OutlineSelectionInstruction;
import net.createmod.ponder1710.foundation.instruction.PonderInstruction;
import net.createmod.ponder1710.foundation.instruction.ReplaceBlocksInstruction;
import net.createmod.ponder1710.foundation.instruction.RotateSceneInstruction;
import net.createmod.ponder1710.foundation.instruction.ShowInputInstruction;
import net.createmod.ponder1710.foundation.instruction.TextInstruction;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PonderSceneBuilder implements SceneBuilder {

    private final OverlayInstructions overlay;
    private final WorldInstructions world;
    private final DebugInstructions debug;
    private final EffectInstructions effects;
    private final SpecialInstructions special;

    protected final PonderScene scene;

    public PonderSceneBuilder(PonderScene ponderScene) {
        scene = ponderScene;
        overlay = new PonderOverlayInstructions();
        special = new PonderSpecialInstructions();
        world = new PonderWorldInstructions();
        debug = new PonderDebugInstructions();
        effects = new PonderEffectInstructions();
    }

    @Override public OverlayInstructions overlay() { return overlay; }
    @Override public WorldInstructions world() { return world; }
    @Override public DebugInstructions debug() { return debug; }
    @Override public EffectInstructions effects() { return effects; }
    @Override public SpecialInstructions special() { return special; }
    @Override public PonderScene getScene() { return scene; }

    @Override
    public void title(String sceneId, String title) {
        // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
        scene.sceneId = new ResourceLocation(scene.getNamespace(), sceneId);
        scene.localization.registerSpecific(scene.sceneId, PonderScene.TITLE_KEY, title);
    }

    @Override
    public void configureBasePlate(int xOffset, int zOffset, int basePlateSize) {
        scene.basePlateOffsetX = xOffset;
        scene.basePlateOffsetZ = zOffset;
        scene.basePlateSize = basePlateSize;
    }

    @Override
    public void scaleSceneView(float factor) { scene.scaleFactor = factor; }

    @Override
    public void removeShadow() { scene.hidePlatformShadow = true; }

    @Override
    public void setSceneOffsetY(float yOffset) { scene.yOffset = yOffset; }

    @Override
    public void showBasePlate() {
        world.showSection(scene.getSceneBuildingUtil().select().cuboid(
            scene.getBasePlateOffsetX(), 0, scene.getBasePlateOffsetZ(),
            scene.getBasePlateSize()-1, 0, scene.getBasePlateSize()-1),
            ForgeDirection.UP);
    }

    @Override
    public void addInstruction(PonderInstruction instruction) { scene.schedule.add(instruction); }

    @Override
    public void addInstruction(Consumer<PonderScene> callback) { addInstruction(PonderInstruction.simple(callback)); }

    @Override
    public void idle(int ticks) { addInstruction(new DelayInstruction(ticks)); }

    @Override
    public void idleSeconds(int seconds) { idle(seconds * 20); }

    @Override
    public void markAsFinished() { addInstruction(new MarkAsFinishedInstruction()); }

    @Override
    public void setNextUpEnabled(boolean isEnabled) { addInstruction(s -> s.setNextUpEnabled(isEnabled)); }

    @Override
    public void rotateCameraY(float degrees) { addInstruction(new RotateSceneInstruction(0, degrees, true)); }

    @Override
    public void addKeyframe() { addInstruction(KeyframeInstruction.IMMEDIATE); }

    @Override
    public void addLazyKeyframe() { addInstruction(KeyframeInstruction.DELAYED); }

    public class PonderEffectInstructions implements EffectInstructions {

        @Override
        public void emitParticles(Vec3 location, ParticleEmitter emitter, float amountPerCycle, int cycles) {
            addInstruction(new EmitParticlesInstruction(location, emitter, amountPerCycle, cycles));
        }

        @Override
        public void indicateRedstone(int x, int y, int z) {
            createRedstoneParticles(x, y, z, 0xFF0000, 10);
        }

        @Override
        public void indicateSuccess(int x, int y, int z) {
            createRedstoneParticles(x, y, z, 0x80FFaa, 10);
        }

        @Override
        public void createRedstoneParticles(int x, int y, int z, int color, int amount) {
            // TODO: DustParticleOptions not available in 1.7.10 - particle system different
            Vec3 center = Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);
            addInstruction(new EmitParticlesInstruction(center,
                (w, px, py, pz) -> { /* TODO: add particles in 1.7.10 */ }, amount, 2));
        }
    }

    public class PonderOverlayInstructions implements OverlayInstructions {

        @Override
        public TextElementBuilder showText(int duration) {
            TextWindowElement textWindowElement = new TextWindowElement();
            addInstruction(new TextInstruction(textWindowElement, duration));
            return textWindowElement.builder(scene);
        }

        @Override
        public TextElementBuilder showOutlineWithText(Selection selection, int duration) {
            TextWindowElement textWindowElement = new TextWindowElement();
            addInstruction(new TextInstruction(textWindowElement, duration, selection));
            return textWindowElement.builder(scene).pointAt(selection.getCenter());
        }

        @Override
        public InputElementBuilder showControls(Vec3 sceneSpace, ForgeDirection direction, int duration) {
            InputWindowElement inputWindowElement = new InputWindowElement(sceneSpace, direction);
            addInstruction(new ShowInputInstruction(inputWindowElement, duration));
            return inputWindowElement.builder();
        }

        @Override
        public void chaseBoundingBoxOutline(PonderPalette color, Object slot, AxisAlignedBB boundingBox, int duration) {
            addInstruction(new ChaseAABBInstruction(color, slot, boundingBox, duration));
        }

        @Override
        public void showCenteredScrollInput(int x, int y, int z, ForgeDirection side, int duration) {
            showScrollInput(scene.getSceneBuildingUtil().vector().blockSurface(x, y, z, side), side, duration);
        }

        @Override
        public void showScrollInput(Vec3 location, ForgeDirection side, int duration) {
            float s = 1 / 16f;
            float q = 1 / 4f;
            Vec3 expands = Vec3.createVectorHelper(
                side.offsetX != 0 ? s : q,
                side.offsetY != 0 ? s : q,
                side.offsetZ != 0 ? s : q
            );
            addInstruction(new HighlightValueBoxInstruction(location, expands, duration));
        }

        @Override
        public void showRepeaterScrollInput(int x, int y, int z, int duration) {
            float s = 1 / 16f;
            float q = 1 / 6f;
            Vec3 expands = Vec3.createVectorHelper(q, s, q);
            Vec3 pos = scene.getSceneBuildingUtil().vector().blockSurface(x, y, z, ForgeDirection.DOWN);
            addInstruction(new HighlightValueBoxInstruction(
                Vec3.createVectorHelper(pos.xCoord, pos.yCoord + 3/16f, pos.zCoord), expands, duration));
        }

        @Override
        public void showFilterSlotInput(Vec3 location, int duration) {
            float s = .1f;
            addInstruction(new HighlightValueBoxInstruction(location, Vec3.createVectorHelper(s, s, s), duration));
        }

        @Override
        public void showFilterSlotInput(Vec3 location, ForgeDirection side, int duration) {
            Vec3 loc = Vec3.createVectorHelper(
                location.xCoord + side.offsetX * (-3/128f),
                location.yCoord + side.offsetY * (-3/128f),
                location.zCoord + side.offsetZ * (-3/128f)
            );
            // TODO: VecHelper.axisAlingedPlaneOf not available
            addInstruction(new HighlightValueBoxInstruction(loc, Vec3.createVectorHelper(11/128f, 11/128f, 11/128f), duration));
        }

        @Override
        public void showLine(PonderPalette color, Vec3 start, Vec3 end, int duration) {
            addInstruction(new LineInstruction(color, start, end, duration, false));
        }

        @Override
        public void showBigLine(PonderPalette color, Vec3 start, Vec3 end, int duration) {
            addInstruction(new LineInstruction(color, start, end, duration, true));
        }

        @Override
        public void showOutline(PonderPalette color, Object slot, Selection selection, int duration) {
            addInstruction(new OutlineSelectionInstruction(color, slot, selection, duration));
        }
    }

    public class PonderSpecialInstructions implements SpecialInstructions {

        @Override
        public void movePointOfInterest(Vec3 location) {
            addInstruction(new MovePoiInstruction(location));
        }

        @Override
        public void movePointOfInterest(int x, int y, int z) {
            movePointOfInterest(Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5));
        }

        @Override
        public ElementLink<MinecartElement> createCart(Vec3 location, float angle, MinecartConstructor type) {
            ElementLink<MinecartElement> link = new ElementLinkImpl<>(MinecartElement.class);
            MinecartElement cart = new MinecartElementImpl(location, angle, type);
            addInstruction(new CreateMinecartInstruction(10, ForgeDirection.DOWN, cart));
            addInstruction(s -> s.linkElement(cart, link));
            return link;
        }

        @Override
        public void rotateCart(ElementLink<MinecartElement> link, float yRotation, int duration) {
            addInstruction(AnimateMinecartInstruction.rotate(link, yRotation, duration));
        }

        @Override
        public void moveCart(ElementLink<MinecartElement> link, Vec3 offset, int duration) {
            addInstruction(AnimateMinecartInstruction.move(link, offset, duration));
        }

        @Override
        public <T extends AnimatedSceneElement> void hideElement(ElementLink<T> link, ForgeDirection direction) {
            addInstruction(new FadeOutOfSceneInstruction<>(15, direction, link));
        }
    }

    public class PonderWorldInstructions implements WorldInstructions {

        @Override
        public void incrementBlockBreakingProgress(int x, int y, int z) {
            addInstruction(s -> {
                PonderLevel world = s.getWorld();
                long key = PonderLevel.posToLong(x, y, z);
                int progress = world.getBlockBreakingProgressions().getOrDefault(key, -1) + 1;
                if (progress == 9) {
                    // world.addBlockDestroyEffects(x, y, z, block, meta);
                    // world.setBlock(x, y, z, Blocks.air, 0, 3);
                    world.getBlockBreakingProgressions().remove(key);
                    s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                } else {
                    world.getBlockBreakingProgressions().put(key, progress + 1);
                }
            });
        }

        @Override
        public void showSection(Selection selection, ForgeDirection fadeInDirection) {
            addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection, selection, scene::getBaseWorldSection));
        }

        @Override
        public void showSectionAndMerge(Selection selection, ForgeDirection fadeInDirection, ElementLink<WorldSectionElement> link) {
            addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection, selection, () -> scene.resolve(link)));
        }

        @Override
        public void glueBlockOnto(int x, int y, int z, ForgeDirection fadeInDirection, ElementLink<WorldSectionElement> link) {
            addInstruction(new DisplayWorldSectionInstruction(15, fadeInDirection,
                scene.getSceneBuildingUtil().select().position(x, y, z), () -> scene.resolve(link), new int[]{x, y, z}));
        }

        @Override
        public ElementLink<WorldSectionElement> showIndependentSection(Selection selection, ForgeDirection fadeInDirection) {
            DisplayWorldSectionInstruction instruction = new DisplayWorldSectionInstruction(15, fadeInDirection, selection, null);
            addInstruction(instruction);
            return instruction.createLink(scene);
        }

        @Override
        public ElementLink<WorldSectionElement> showIndependentSectionImmediately(Selection selection) {
            DisplayWorldSectionInstruction instruction = new DisplayWorldSectionInstruction(0, ForgeDirection.DOWN, selection, null);
            addInstruction(instruction);
            return instruction.createLink(scene);
        }

        @Override
        public void hideSection(Selection selection, ForgeDirection fadeOutDirection) {
            WorldSectionElement wse = new WorldSectionElementImpl(selection);
            ElementLink<WorldSectionElement> link = new ElementLinkImpl<>(WorldSectionElement.class);
            addInstruction(s -> {
                s.getBaseWorldSection().erase(selection);
                s.linkElement(wse, link);
                s.addElement(wse);
                wse.queueRedraw();
            });
            hideIndependentSection(link, fadeOutDirection);
        }

        @Override
        public void hideIndependentSection(ElementLink<WorldSectionElement> link, ForgeDirection fadeOutDirection) {
            addInstruction(new FadeOutOfSceneInstruction<>(15, fadeOutDirection, link));
        }

        @Override
        public void restoreBlocks(Selection selection) {
            addInstruction(s -> s.getWorld().restore());
        }

        @Override
        public ElementLink<WorldSectionElement> makeSectionIndependent(Selection selection) {
            WorldSectionElementImpl wse = new WorldSectionElementImpl(selection);
            ElementLink<WorldSectionElement> link = new ElementLinkImpl<>(WorldSectionElement.class);
            addInstruction(s -> {
                s.getBaseWorldSection().erase(selection);
                s.linkElement(wse, link);
                s.addElement(wse);
                wse.queueRedraw();
                wse.resetAnimatedTransform();
                wse.setVisible(true);
                wse.forceApplyFade(1);
            });
            return link;
        }

        @Override
        public void rotateSection(ElementLink<WorldSectionElement> link, double xRotation, double yRotation, double zRotation, int duration) {
            addInstruction(AnimateWorldSectionInstruction.rotate(link, Vec3.createVectorHelper(xRotation, yRotation, zRotation), duration));
        }

        @Override
        public void configureCenterOfRotation(ElementLink<WorldSectionElement> link, Vec3 anchor) {
            addInstruction(s -> s.resolveOptional(link).ifPresent(safe -> safe.setCenterOfRotation(anchor)));
        }

        @Override
        public void configureStabilization(ElementLink<WorldSectionElement> link, Vec3 anchor) {
            addInstruction(s -> s.resolveOptional(link).ifPresent(safe -> safe.stabilizeRotation(anchor)));
        }

        @Override
        public void moveSection(ElementLink<WorldSectionElement> link, Vec3 offset, int duration) {
            addInstruction(AnimateWorldSectionInstruction.move(link, offset, duration));
        }

        @Override
        public void setBlocks(Selection selection, Block block, int meta, boolean spawnParticles) {
            addInstruction(new ReplaceBlocksInstruction(selection, block, meta, true, spawnParticles));
        }

        @Override
        public void destroyBlock(int x, int y, int z) {
            // TODO: Blocks.air in 1.7.10
            // setBlocks(scene.getSceneBuildingUtil().select().position(x, y, z), Blocks.air, 0, true);
        }

        @Override
        public void setBlock(int x, int y, int z, Block block, int meta, boolean spawnParticles) {
            setBlocks(scene.getSceneBuildingUtil().select().position(x, y, z), block, meta, spawnParticles);
        }

        @Override
        public void replaceBlocks(Selection selection, Block block, int meta, boolean spawnParticles) {
            setBlocks(selection, block, meta, spawnParticles);
        }

        @Override
        public void toggleRedstonePower(Selection selection) {
            // TODO: BlockState properties not available in 1.7.10
        }

        @Override
        public <T extends Entity> void modifyEntities(Class<T> entityClass, Consumer<T> entityCallBack) {
            addInstruction(s -> s.forEachWorldEntity(entityClass, entityCallBack));
        }

        @Override
        public <T extends Entity> void modifyEntitiesInside(Class<T> entityClass, Selection area, Consumer<T> entityCallBack) {
            addInstruction(s -> s.forEachWorldEntity(entityClass, e -> {
                // TODO: area.test with entity position
                entityCallBack.accept(e);
            }));
        }

        @Override
        public void modifyEntity(ElementLink<EntityElement> link, Consumer<Entity> entityCallBack) {
            addInstruction(s -> {
                EntityElement resolve = s.resolve(link);
                if (resolve != null)
                    resolve.ifPresent(entityCallBack);
            });
        }

        @Override
        public ElementLink<EntityElement> createEntity(Function<World, Entity> factory) {
            ElementLink<EntityElement> link = new ElementLinkImpl<>(EntityElement.class, UUID.randomUUID());
            addInstruction(s -> {
                // TODO: PonderLevel needs World integration
                Entity entity = factory.apply(null);
                EntityElement handle = new EntityElementImpl(entity);
                s.addElement(handle);
                s.linkElement(handle, link);
                s.getWorld().getEntityList().add(entity);
            });
            return link;
        }

        @Override
        public ElementLink<EntityElement> createItemEntity(Vec3 location, Vec3 motion, ItemStack stack) {
            return createEntity(world -> {
                // TODO: EntityItem constructor different in 1.7.10
                EntityItem itemEntity = new EntityItem(null, location.xCoord, location.yCoord, location.zCoord, stack);
                itemEntity.motionX = motion.xCoord;
                itemEntity.motionY = motion.yCoord;
                itemEntity.motionZ = motion.zCoord;
                return itemEntity;
            });
        }

        @Override
        public void modifyTileEntityNBT(Selection selection, Class<? extends TileEntity> teType, Consumer<NBTTagCompound> consumer) {
            modifyTileEntityNBT(selection, teType, consumer, false);
        }

        @Override
        public <T extends TileEntity> void modifyTileEntity(int x, int y, int z, Class<T> teType, Consumer<T> consumer) {
            addInstruction(new BlockEntityDataInstruction(
                scene.getSceneBuildingUtil().select().position(x, y, z), teType,
                nbt -> { consumer.accept(null); return nbt; }, false));
        }

        @Override
        public void modifyTileEntityNBT(Selection selection, Class<? extends TileEntity> teType, Consumer<NBTTagCompound> consumer, boolean reDrawBlocks) {
            addInstruction(new BlockEntityDataInstruction(selection, teType, nbt -> { consumer.accept(nbt); return nbt; }, reDrawBlocks));
        }
    }

    public class PonderDebugInstructions implements DebugInstructions {

        @Override
        public void debugSchematic() {
            addInstruction(s -> s.addElement(new WorldSectionElementImpl(s.getSceneBuildingUtil().select().everywhere())));
        }

        @Override
        public void addInstructionInstance(PonderInstruction instruction) {
            addInstruction(instruction);
        }

        @Override
        public void enqueueCallback(Consumer<PonderScene> callback) {
            addInstruction(callback);
        }
    }
}
