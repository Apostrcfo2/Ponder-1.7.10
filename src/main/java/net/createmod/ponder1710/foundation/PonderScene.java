package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

// import org.joml.Matrix4f; // Not available in 1.7.10
// import org.joml.Vector4f; // Not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // Not available in 1.7.10 - use GL11
// import net.createmod.metanip.animation.AnimationTickHolder; // catnip not available
// import net.createmod.metanip.animation.LerpedFloat; // catnip not available - replaced with float
// import net.createmod.metanip.data.Pair; // catnip not available
// import net.createmod.metanip.gui.UIRenderHelper; // catnip not available
// import net.createmod.metanip.math.VecHelper; // catnip not available
// import net.createmod.metanip.outliner.Outliner; // catnip not available - TODO: port or reimplement
// import net.createmod.metanip.platform.CatnipServices; // catnip not available
// import net.createmod.metanip.render.SuperRenderTypeBuffer; // catnip not available

import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.PonderElement;
import net.createmod.ponder1710.api.element.PonderOverlayElement;
import net.createmod.ponder1710.api.element.PonderSceneElement;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;
import net.createmod.ponder1710.foundation.element.WorldSectionElementImpl;
import net.createmod.ponder1710.foundation.instruction.HideAllInstruction;
import net.createmod.ponder1710.foundation.instruction.PonderInstruction;
import net.createmod.ponder1710.foundation.registration.PonderLocalization;
import net.createmod.ponder1710.foundation.ui.PonderUI;

// 1.7.10 equivalents
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityArmorStand; // ArmorStand in 1.7.10
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

// Commented out - not available in 1.7.10:
// import net.minecraft.client.Camera; // No Camera class in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // Not available - use GL11
// import net.minecraft.client.renderer.RenderType; // Not available in 1.7.10
// import net.minecraft.core.BlockPos; // 1.7.10 uses ChunkCoordinates or x,y,z
// import net.minecraft.core.Direction; // 1.7.10 uses ForgeDirection
// import net.minecraft.core.Direction.Axis; // 1.7.10 uses ForgeDirection
// import net.minecraft.core.Vec3i; // Not available in 1.7.10
// import net.minecraft.world.entity.Entity; // Different package in 1.7.10
// import net.minecraft.world.entity.decoration.ArmorStand; // Different in 1.7.10
// import net.minecraft.world.item.ItemStack; // Different package in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // Not available in 1.7.10
// import net.minecraft.world.level.levelgen.structure.BoundingBox; // Not available in 1.7.10
// import net.minecraft.world.phys.BlockHitResult; // Not available in 1.7.10
// import net.minecraft.world.phys.Vec2; // Not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // Different package in 1.7.10

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

// TODO: StoryBoardEntry.SceneOrderingEntry needs to be checked
import net.createmod.ponder1710.api.registration.StoryBoardEntry.SceneOrderingEntry;

public class PonderScene {

    public static final String TITLE_KEY = "header";

    final PonderLocalization localization;

    private boolean finished;
    private int textIndex;
    ResourceLocation sceneId;

    private final IntList keyframeTimes;

    List<PonderInstruction> schedule;
    private final List<PonderInstruction> activeSchedule;
    private final Map<UUID, PonderElement> linkedElements;
    private final Set<PonderElement> elements;
    private final List<PonderTag> tags;
    private final List<SceneOrderingEntry> orderingEntries;

    private final PonderLevel world;
    private final String namespace;
    private final ResourceLocation location;
    // private final SceneCamera camera; // TODO: Camera not available in 1.7.10
    // private final Outliner outliner; // TODO: Outliner from catnip - needs port
    private SceneTransform transform;

    private final WorldSectionElement baseWorldSection;
    // private final Entity renderViewEntity; // TODO: ArmorStand entity needs checking
    private Vec3 pointOfInterest;
    @Nullable
    private Vec3 chasingPointOfInterest;

    int basePlateOffsetX;
    int basePlateOffsetZ;
    int basePlateSize;
    float scaleFactor;
    float yOffset;
    boolean hidePlatformShadow;

    private boolean stoppedCounting;
    private int totalTime;
    private int currentTime;
    private boolean nextUpEnabled = true;

    public PonderScene(@Nullable PonderLevel world, PonderLocalization localization, String namespace,
        ResourceLocation location, Collection<ResourceLocation> tags,
        Collection<SceneOrderingEntry> orderingEntries) {
        if (world != null) {
            world.scene = this;
        }
        this.world = world;
        this.localization = localization;

        pointOfInterest = Vec3.createVectorHelper(0, 4, 0);
        textIndex = 1;
        hidePlatformShadow = false;

        this.namespace = namespace;
        this.location = location;
        this.sceneId = new ResourceLocation(namespace, "missing_title");

        // outliner = new Outliner(); // TODO: port catnip Outliner
        elements = new HashSet<>();
        linkedElements = new HashMap<>();
        this.tags = new ArrayList<>(); // TODO: tag registry lookup
        this.orderingEntries = new ArrayList<>(orderingEntries);
        schedule = new ArrayList<>();
        activeSchedule = new ArrayList<>();
        transform = new SceneTransform();
        // basePlateSize = getBounds().getXSpan(); // TODO: BoundingBox not available
        basePlateSize = 5; // default
        // camera = new SceneCamera(); // TODO: Camera not available in 1.7.10
        baseWorldSection = new WorldSectionElementImpl();
        keyframeTimes = new IntArrayList(4);
        scaleFactor = 1;
        yOffset = 0;

        // renderViewEntity = new ArmorStand(world, 0, 0, 0); // TODO: check ArmorStand in 1.7.10
        setPointOfInterest(Vec3.createVectorHelper(0, 4, 0));
    }

    public void reset() {
        currentTime = 0;
        activeSchedule.clear();
        schedule.forEach(mdi -> mdi.reset(this));
    }

    public void begin() {
        reset();
        forEach(pe -> pe.reset(this));

        world.restore();
        elements.clear();
        linkedElements.clear();
        keyframeTimes.clear();

        transform = new SceneTransform();
        finished = false;
        setPointOfInterest(Vec3.createVectorHelper(0, 4, 0));

        baseWorldSection.setEmpty();
        baseWorldSection.forceApplyFade(1);
        elements.add(baseWorldSection);

        totalTime = 0;
        stoppedCounting = false;
        activeSchedule.addAll(schedule);
        activeSchedule.forEach(i -> i.onScheduled(this));
    }

    public WorldSectionElement getBaseWorldSection() {
        return baseWorldSection;
    }

    public float getSceneProgress() {
        return totalTime == 0 ? 0 : currentTime / (float) totalTime;
    }

    public void fadeOut() {
        reset();
        activeSchedule.add(new HideAllInstruction(10, null));
    }

    // TODO: renderScene - needs GL11 port of PoseStack and SuperRenderTypeBuffer
    // public void renderScene(SuperRenderTypeBuffer buffer, GuiGraphics graphics, float pt) { ... }

    // TODO: renderOverlay - needs GuiGraphics port
    // public void renderOverlay(PonderUI screen, GuiGraphics graphics, float partialTicks) { ... }

    public void setPointOfInterest(Vec3 poi) {
        if (chasingPointOfInterest == null)
            pointOfInterest = poi;
        chasingPointOfInterest = poi;
    }

    public Vec3 getPointOfInterest() {
        return pointOfInterest;
    }

    public void tick() {
        if (chasingPointOfInterest != null) {
            // TODO: VecHelper.lerp not available - manual lerp
            pointOfInterest = lerpVec(.25f, pointOfInterest, chasingPointOfInterest);
        }

        // outliner.tickOutlines(); // TODO: catnip Outliner
        world.tick();
        transform.tick();
        forEach(e -> e.tick(this));

        if (currentTime < totalTime)
            currentTime++;

        for (Iterator<PonderInstruction> iterator = activeSchedule.iterator(); iterator.hasNext();) {
            PonderInstruction instruction = iterator.next();
            instruction.tick(this);
            if (instruction.isComplete()) {
                iterator.remove();
                if (instruction.isBlocking())
                    break;
                continue;
            }
            if (instruction.isBlocking())
                break;
        }

        if (activeSchedule.isEmpty())
            finished = true;
    }

    // Manual lerp since VecHelper not available
    private Vec3 lerpVec(float t, Vec3 a, Vec3 b) {
        return Vec3.createVectorHelper(
            a.xCoord + (b.xCoord - a.xCoord) * t,
            a.yCoord + (b.yCoord - a.yCoord) * t,
            a.zCoord + (b.zCoord - a.zCoord) * t
        );
    }

    public void addToSceneTime(int time) {
        if (!stoppedCounting)
            totalTime += time;
    }

    public void stopCounting() {
        stoppedCounting = true;
    }

    public void markKeyframe(int offset) {
        if (!stoppedCounting)
            keyframeTimes.add(totalTime + offset);
    }

    public void addElement(PonderElement e) {
        elements.add(e);
    }

    public <E extends PonderElement> void linkElement(E e, ElementLink<E> link) {
        linkedElements.put(link.getId(), e);
    }

    @Nullable
    public <E extends PonderElement> E resolve(ElementLink<E> link) {
        return link.cast(linkedElements.get(link.getId()));
    }

    public <E extends PonderElement> Optional<E> resolveOptional(ElementLink<E> link) {
        return Optional.ofNullable(resolve(link));
    }

    public <E extends PonderElement> void runWith(ElementLink<E> link, Consumer<E> callback) {
        callback.accept(resolve(link));
    }

    public <E extends PonderElement, F> F applyTo(ElementLink<E> link, Function<E, F> function) {
        return function.apply(resolve(link));
    }

    public void forEach(Consumer<? super PonderElement> function) {
        for (PonderElement element : elements)
            function.accept(element);
    }

    public <T extends PonderElement> void forEach(Class<T> type, Consumer<T> function) {
        for (PonderElement element : elements)
            if (type.isInstance(element))
                function.accept(type.cast(element));
    }

    public <T extends PonderElement> void forEachVisible(Class<T> type, Consumer<T> function) {
        for (PonderElement element : elements)
            if (type.isInstance(element) && element.isVisible())
                function.accept(type.cast(element));
    }

    public Supplier<String> registerText(String defaultText) {
        final String key = "text_" + textIndex;
        localization.registerSpecific(sceneId, key, defaultText);
        Supplier<String> supplier = () -> localization.getSpecific(sceneId, key);
        textIndex++;
        return supplier;
    }

    public SceneBuilder builder() {
        return new PonderSceneBuilder(this);
    }

    public SceneBuildingUtil getSceneBuildingUtil() {
        return new PonderSceneBuildingUtil(null); // TODO: getBounds()
    }

    public String getTitle() {
        return getString(TITLE_KEY);
    }

    public String getString(String key) {
        return localization.getSpecific(sceneId, key);
    }

    public PonderLevel getWorld() {
        return world;
    }

    public String getNamespace() {
        return namespace;
    }

    public int getKeyframeCount() {
        return keyframeTimes.size();
    }

    public int getKeyframeTime(int index) {
        return keyframeTimes.getInt(index);
    }

    public List<PonderTag> getTags() {
        return tags;
    }

    public List<SceneOrderingEntry> getOrderingEntries() {
        return orderingEntries;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public Set<PonderElement> getElements() {
        return elements;
    }

    // TODO: getBounds() - BoundingBox not available in 1.7.10
    // public BoundingBox getBounds() { ... }

    public ResourceLocation getId() {
        return sceneId;
    }

    public SceneTransform getTransform() {
        return transform;
    }

    // TODO: getOutliner() - catnip Outliner not available
    // public Outliner getOutliner() { return outliner; }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getBasePlateOffsetX() {
        return basePlateOffsetX;
    }

    public int getBasePlateOffsetZ() {
        return basePlateOffsetZ;
    }

    public boolean shouldHidePlatformShadow() {
        return hidePlatformShadow;
    }

    public int getBasePlateSize() {
        return basePlateSize;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public float getYOffset() {
        return yOffset;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setNextUpEnabled(boolean nextUpEnabled) {
        this.nextUpEnabled = nextUpEnabled;
    }

    public boolean isNextUpEnabled() {
        return nextUpEnabled;
    }

    // Simplified SceneTransform for 1.7.10 - replaces LerpedFloat with plain floats
    // TODO: Add smooth interpolation once LerpedFloat is ported from catnip
    public class SceneTransform {

        public float xRotation = -35f;
        public float yRotation = 55f + 90f;

        public void tick() {
            // TODO: LerpedFloat.tickChaser() not available - smooth rotation pending catnip port
        }
    }

    // TODO: SceneCamera - Camera class not available in 1.7.10
    // public static class SceneCamera extends Camera { ... }
}
