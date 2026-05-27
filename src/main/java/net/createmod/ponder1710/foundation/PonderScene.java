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

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.data.Pair;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.math.VecHelper;
import net.createmod.metanip.outliner.Outliner;

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
import net.createmod.ponder1710.api.registration.StoryBoardEntry.SceneOrderingEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

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
    private final Outliner outliner;
    private SceneTransform transform;

    private final WorldSectionElement baseWorldSection;
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
        if (world != null)
            world.scene = this;
        this.world = world;
        this.localization = localization;

        pointOfInterest = Vec3.createVectorHelper(0, 4, 0);
        textIndex = 1;
        hidePlatformShadow = false;

        this.namespace = namespace;
        this.location = location;
        this.sceneId = new ResourceLocation(namespace, "missing_title");

        outliner = new Outliner();
        elements = new HashSet<>();
        linkedElements = new HashMap<>();
        this.tags = new ArrayList<>();
        this.orderingEntries = new ArrayList<>(orderingEntries);
        schedule = new ArrayList<>();
        activeSchedule = new ArrayList<>();
        transform = new SceneTransform();
        basePlateSize = world != null ? getBounds()[3] - getBounds()[0] + 1 : 5;
        baseWorldSection = new WorldSectionElementImpl();
        keyframeTimes = new IntArrayList(4);
        scaleFactor = 1;
        yOffset = 0;

        setPointOfInterest(Vec3.createVectorHelper(0, 4, 0));
    }

    // bounds as int[] {minX, minY, minZ, maxX, maxY, maxZ}
    public int[] getBounds() {
        return world != null ? world.getBounds() : new int[]{0, 0, 0, 4, 4, 4};
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

    public WorldSectionElement getBaseWorldSection() { return baseWorldSection; }

    public float getSceneProgress() {
        return totalTime == 0 ? 0 : currentTime / (float) totalTime;
    }

    public void fadeOut() {
        reset();
        activeSchedule.add(new HideAllInstruction(10, null));
    }

    public void renderScene(float pt) {
        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        Entity prevRVE = mc.renderViewEntity;

        // Set render view entity to scene center for proper rendering
        if (world != null) {
            Vec3 poi = getPointOfInterest();
            mc.renderViewEntity.setPosition(poi.xCoord, poi.yCoord, poi.zCoord);
        }

        forEachVisible(PonderSceneElement.class, e -> e.renderFirst(world, pt));

        // Render block layers
        forEachVisible(PonderSceneElement.class, e -> e.renderLayer(world, pt));

        forEachVisible(PonderSceneElement.class, e -> e.renderLast(world, pt));

        // Camera angles from transform
        // In 1.7.10 the camera is implicit via GL11 transforms applied in SceneTransform
        world.renderEntities(pt);
        world.renderParticles(pt);

        outliner.renderOutlines(new Matrix4f(), Vec3.createVectorHelper(0, 0, 0), pt);

        mc.renderViewEntity = prevRVE;
        GL11.glPopMatrix();
    }

    public void renderOverlay(PonderUI screen, float partialTicks) {
        GL11.glPushMatrix();
        forEachVisible(PonderOverlayElement.class, e -> e.render(this, screen, partialTicks));
        GL11.glPopMatrix();
    }

    public Pair<ItemStack, int[]> rayTraceScene(Vec3 from, Vec3 to) {
        // TODO: implement ray tracing for identify mode
        return Pair.of(null, null);
    }

    public void deselect() {
        forEach(WorldSectionElement.class, WorldSectionElement::resetSelectedBlock);
    }

    public void setPointOfInterest(Vec3 poi) {
        if (chasingPointOfInterest == null)
            pointOfInterest = poi;
        chasingPointOfInterest = poi;
    }

    public Vec3 getPointOfInterest() { return pointOfInterest; }

    public void tick() {
        if (chasingPointOfInterest != null)
            pointOfInterest = VecHelper.lerp(.25f, pointOfInterest, chasingPointOfInterest);

        outliner.tickOutlines();
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
                if (instruction.isBlocking()) break;
                continue;
            }
            if (instruction.isBlocking()) break;
        }

        if (activeSchedule.isEmpty())
            finished = true;
    }

    public void seekToTime(int time) {
        if (time < currentTime)
            throw new IllegalStateException("Cannot seek backwards. Rewind first.");
        while (currentTime < time && !finished) {
            forEach(e -> e.whileSkipping(this));
            tick();
        }
        forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
    }

    public void addToSceneTime(int time) {
        if (!stoppedCounting) totalTime += time;
    }

    public void stopCounting() { stoppedCounting = true; }

    public void markKeyframe(int offset) {
        if (!stoppedCounting) keyframeTimes.add(totalTime + offset);
    }

    public void addElement(PonderElement e) { elements.add(e); }

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
        for (PonderElement element : elements) function.accept(element);
    }

    public <T extends PonderElement> void forEach(Class<T> type, Consumer<T> function) {
        for (PonderElement element : elements)
            if (type.isInstance(element)) function.accept(type.cast(element));
    }

    public <T extends PonderElement> void forEachVisible(Class<T> type, Consumer<T> function) {
        for (PonderElement element : elements)
            if (type.isInstance(element) && element.isVisible()) function.accept(type.cast(element));
    }

    public Supplier<String> registerText(String defaultText) {
        final String key = "text_" + textIndex;
        localization.registerSpecific(sceneId, key, defaultText);
        Supplier<String> supplier = () -> localization.getSpecific(sceneId, key);
        textIndex++;
        return supplier;
    }

    public SceneBuilder builder() { return new PonderSceneBuilder(this); }

    public SceneBuildingUtil getSceneBuildingUtil() {
        int[] bounds = getBounds();
        return new PonderSceneBuildingUtil(bounds);
    }

    public String getTitle() { return getString(TITLE_KEY); }

    public String getString(String key) { return localization.getSpecific(sceneId, key); }

    public PonderLevel getWorld() { return world; }
    public String getNamespace() { return namespace; }
    public int getKeyframeCount() { return keyframeTimes.size(); }
    public int getKeyframeTime(int index) { return keyframeTimes.getInt(index); }
    public List<PonderTag> getTags() { return tags; }
    public List<SceneOrderingEntry> getOrderingEntries() { return orderingEntries; }
    public ResourceLocation getLocation() { return location; }
    public Set<PonderElement> getElements() { return elements; }
    public ResourceLocation getId() { return sceneId; }
    public SceneTransform getTransform() { return transform; }
    public Outliner getOutliner() { return outliner; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public int getBasePlateOffsetX() { return basePlateOffsetX; }
    public int getBasePlateOffsetZ() { return basePlateOffsetZ; }
    public boolean shouldHidePlatformShadow() { return hidePlatformShadow; }
    public int getBasePlateSize() { return basePlateSize; }
    public float getScaleFactor() { return scaleFactor; }
    public float getYOffset() { return yOffset; }
    public int getTotalTime() { return totalTime; }
    public int getCurrentTime() { return currentTime; }
    public void setNextUpEnabled(boolean e) { nextUpEnabled = e; }
    public boolean isNextUpEnabled() { return nextUpEnabled; }

    // SceneTransform using LerpedFloat from metanip + GL11
    public class SceneTransform {

        public LerpedFloat xRotation;
        public LerpedFloat yRotation;

        private int width, height;
        private double offset;

        public SceneTransform() {
            xRotation = LerpedFloat.angular().disableSmartAngleChasing().startWithValue(-35);
            yRotation = LerpedFloat.angular().disableSmartAngleChasing().startWithValue(55 + 90);
        }

        public void tick() {
            xRotation.tickChaser();
            yRotation.tickChaser();
        }

        public void updateScreenParams(int width, int height, double offset) {
            this.width = width;
            this.height = height;
            this.offset = offset;
        }

        // Apply GL11 transforms equivalent to PoseStack.apply()
        public void apply(float pt) {
            GL11.glTranslatef(width / 2f, height / 2f, 200f + (float)offset);

            GL11.glRotatef(-35, 1, 0, 0);
            GL11.glRotatef(55, 0, 1, 0);
            GL11.glTranslatef((float)offset, 0, 0);
            GL11.glRotatef(-55, 0, 1, 0);
            GL11.glRotatef(35, 1, 0, 0);
            GL11.glRotatef(xRotation.getValue(pt), 1, 0, 0);
            GL11.glRotatef(yRotation.getValue(pt), 0, 1, 0);

            UIRenderHelper.flipForGuiRender();
            float f = 30 * scaleFactor;
            GL11.glScalef(f, f, f);
            GL11.glTranslatef(
                basePlateSize / -2f - basePlateOffsetX,
                -1f + yOffset,
                basePlateSize / -2f - basePlateOffsetZ
            );
        }

        // Screen to scene coordinate conversion using inverse transforms
        public Vec3 screenToScene(double x, double y, int depth, float pt) {
            Vec3 vec = Vec3.createVectorHelper(x, y, depth);

            vec = Vec3.createVectorHelper(
                vec.xCoord - width / 2.0,
                vec.yCoord - height / 2.0,
                vec.zCoord - (200 + offset)
            );
            vec = VecHelper.rotate(vec, 35, VecHelper.AXIS_X);
            vec = VecHelper.rotate(vec, -55, VecHelper.AXIS_Y);
            vec = Vec3.createVectorHelper(vec.xCoord - offset, vec.yCoord, vec.zCoord);
            vec = VecHelper.rotate(vec, 55, VecHelper.AXIS_Y);
            vec = VecHelper.rotate(vec, -35, VecHelper.AXIS_X);
            vec = VecHelper.rotate(vec, -xRotation.getValue(pt), VecHelper.AXIS_X);
            vec = VecHelper.rotate(vec, -yRotation.getValue(pt), VecHelper.AXIS_Y);

            float f = 1f / (30 * scaleFactor);
            vec = Vec3.createVectorHelper(vec.xCoord * f, vec.yCoord * -f, vec.zCoord * f);
            vec = Vec3.createVectorHelper(
                vec.xCoord - (basePlateSize / -2f - basePlateOffsetX),
                vec.yCoord - (-1f + yOffset),
                vec.zCoord - (basePlateSize / -2f - basePlateOffsetZ)
            );
            return vec;
        }

        public void updateSceneRVE(float pt) {
            // Update render view entity position for proper lighting
            if (world != null) {
                Vec3 v = screenToScene(width / 2.0, height / 2.0, 500, pt);
                // Set position of a dummy entity if needed
            }
        }
    }
}
