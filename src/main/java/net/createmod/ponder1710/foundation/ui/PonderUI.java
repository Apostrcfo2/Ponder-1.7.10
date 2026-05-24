package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

// import org.joml.Matrix4f; // not available in 1.7.10
// import org.joml.Vector3f; // not available in 1.7.10
// import com.google.common.graph.*; // not available in 1.7.10
// import com.mojang.blaze3d.platform.ClipboardManager; // not available in 1.7.10
// import com.mojang.blaze3d.platform.Window; // not available in 1.7.10
// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.VertexSorting; // not available in 1.7.10
// import com.mojang.math.Axis; // not available in 1.7.10
// import net.createmod.metanip.animation.AnimationTickHolder; // TODO: catnip -> metanip
// import net.createmod.metanip.animation.LerpedFloat; // TODO: catnip -> metanip
// import net.createmod.metanip.data.Couple; // TODO: catnip -> metanip
// import net.createmod.metanip.data.Iterate; // TODO: catnip -> metanip
// import net.createmod.metanip.data.Pair; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.NavigatableSimiScreen; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.ScreenOpener; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.UIRenderHelper; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.element.BoxElement; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.element.GuiGameElement; // TODO: catnip -> metanip
// import net.createmod.metanip.gui.widget.BoxWidget; // TODO: catnip -> metanip
// import net.createmod.metanip.lang.ClientFontHelper; // TODO: catnip -> metanip
// import net.createmod.metanip.math.Pointing; // TODO: catnip -> metanip
// import net.createmod.metanip.registry.RegisteredObjectsHelper; // TODO: catnip -> metanip
// import net.createmod.metanip.render.DefaultSuperRenderTypeBuffer; // TODO: catnip -> metanip
// import net.createmod.metanip.render.SuperRenderTypeBuffer; // TODO: catnip -> metanip
// import net.createmod.metanip.theme.Color; // TODO: catnip -> metanip
// import net.minecraft.ChatFormatting; // EnumChatFormatting in 1.7.10
// import net.minecraft.client.Options; // GameSettings in 1.7.10
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.gui.components.events.GuiEventListener; // not available
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10
// import net.minecraft.network.chat.MutableComponent; // not available in 1.7.10
// import net.minecraft.network.chat.Style; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.level.levelgen.structure.BoundingBox; // not available
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.registration.StoryBoardEntry;
import net.createmod.ponder1710.api.registration.StoryBoardEntry.SceneOrderingEntry;
import net.createmod.ponder1710.api.registration.StoryBoardEntry.SceneOrderingType;
import net.createmod.ponder1710.enums.PonderConfig;
import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderChapter;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderScene.SceneTransform;
import net.createmod.ponder1710.foundation.PonderStoryBoardEntry;
import net.createmod.ponder1710.foundation.PonderTag;
import net.createmod.ponder1710.foundation.content.DebugScenes;
import net.createmod.ponder1710.foundation.element.TextWindowElement;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class PonderUI extends AbstractPonderScreen {

    public static int ponderTicks;
    public static float ponderPartialTicksPaused;

    // TODO: Color from catnip not available - using int colors
    public static final int BACKGROUND_TRANSPARENT = 0xdd000000;
    public static final int BACKGROUND_FLAT = 0xff000000;
    public static final int BACKGROUND_IMPORTANT = 0xdd0e0e20;
    public static final int COLOR_IDLE = 0x40ffeedd;
    public static final int COLOR_HOVER = 0x70ffffff;
    public static final int COLOR_HIGHLIGHT = 0xf0ffeedd;
    public static final int MISSING_VANILLA_ENTRY = 0x505000ff;
    public static final int MISSING_MODDED_ENTRY = 0x70984500;

    private final List<PonderScene> scenes;
    private final List<PonderTag> tags;
    private List<PonderButton> tagButtons = new ArrayList<>();
    // TODO: LerpedFloat from catnip not available
    private List<Float> tagFades = new ArrayList<>();
    @Nullable
    PonderChapter chapter = null;

    private boolean userViewMode;
    private boolean identifyMode;
    @Nullable
    private ItemStack hoveredTooltipItem = null;
    // BlockPos -> int[] {x,y,z} in 1.7.10
    @Nullable
    private int[] hoveredBlockPos;
    @Nullable
    private int[] copiedBlockPos;

    // TODO: LerpedFloat from catnip not available - replaced with float
    private float fadeIn = 0;
    private float finishingFlash = 0;
    private float nextUp = 0;
    private int finishingFlashWarmup = 0;
    private int nextUpWarmup = 0;
    private float lazyIndex;
    private int index = 0;
    @Nullable
    private PonderTag referredToByTag;

    private PonderButton left, right, scan, close, replay, slowMode;
    @Nullable
    private PonderButton userMode;
    private int skipCooling = 0;
    private int extendedTickLength = 0;
    private int extendedTickTimer = 0;

    public static PonderUI of(ResourceLocation id) {
        return new PonderUI(PonderIndex.getSceneAccess().compile(id));
    }

    public static PonderUI of(ItemStack item) {
        // TODO: RegisteredObjectsHelper from catnip not available
        return new PonderUI(new ArrayList<>());
    }

    public static PonderUI of(ItemStack item, PonderTag tag) {
        PonderUI ui = of(item);
        ui.referredToByTag = tag;
        return ui;
    }

    protected PonderUI(List<PonderScene> scenes) {
        List<PonderScene> orderedScenes;
        try {
            orderedScenes = orderScenes(scenes);
        } catch (Exception e) {
            Ponder.LOGGER.warn("Unable to sort PonderScenes, using unordered List", e);
            orderedScenes = scenes;
        }
        this.scenes = new ArrayList<>(orderedScenes);

        if (this.scenes.isEmpty()) {
            List<StoryBoardEntry> list = Collections.singletonList(
                new PonderStoryBoardEntry(DebugScenes::empty, Ponder.MOD_ID, "debug/scene_1",
                    new ResourceLocation("stick")));
            this.scenes.addAll(PonderIndex.getSceneAccess().compile(list));
        }

        this.tags = new ArrayList<>();
        this.lazyIndex = 0;
    }

    private List<PonderScene> orderScenes(List<PonderScene> scenes) {
        // TODO: MutableGraph from Guava not available in this version
        // Simplified ordering without graph
        return new ArrayList<>(scenes);
    }

    @Override
    public void initGui() {
        super.initGui();
        tagButtons = new ArrayList<>();
        tagFades = new ArrayList<>();

        // TODO: Full init with buttons - BoxWidget/ScreenOpener from catnip not available
        // TODO: PonderProgressBar init
        // TODO: Key bindings (bindings.keyDrop, keyLeft, etc.)
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (skipCooling > 0)
            skipCooling--;

        // TODO: lazyIndex smooth chasing with LerpedFloat not available
        fadeIn = Math.min(1, fadeIn + 0.1f);

        PonderScene activeScene = scenes.get(index);

        extendedTickLength = 0;
        if (isComfyReadingEnabled())
            activeScene.forEachVisible(TextWindowElement.class, twe -> extendedTickLength = 2);

        if (extendedTickTimer == 0) {
            if (!identifyMode) {
                ponderTicks++;
                if (skipCooling == 0)
                    activeScene.tick();
            }
            extendedTickTimer = extendedTickLength;
        } else {
            extendedTickTimer--;
        }

        if (activeScene.getCurrentTime() == activeScene.getTotalTime() - 1) {
            finishingFlashWarmup = 30;
            nextUpWarmup = 50;
        }

        if (finishingFlashWarmup > 0) {
            finishingFlashWarmup--;
            if (finishingFlashWarmup == 0)
                finishingFlash = 1;
        }

        if (nextUpWarmup > 0) {
            nextUpWarmup--;
            if (nextUpWarmup == 0)
                nextUp = 1;
        }

        finishingFlash = Math.max(0, finishingFlash - 0.1f);
        nextUp = Math.max(0, nextUp - 0.4f);
    }

    public PonderScene getActiveScene() {
        return scenes.get(index);
    }

    public void seekToTime(int time) {
        if (getActiveScene().getCurrentTime() > time)
            replay();
        getActiveScene().seekToTime(time);
        if (time != 0)
            coolDownAfterSkip();
    }

    protected void replay() {
        identifyMode = false;
        scenes.get(index).begin();
    }

    protected boolean scroll(boolean forward) {
        int prevIndex = index;
        index = forward ? index + 1 : index - 1;
        index = MathHelper.clamp_int(index, 0, scenes.size() - 1);
        if (prevIndex != index) {
            scenes.get(prevIndex).fadeOut();
            scenes.get(index).begin();
            lazyIndex = index;
            identifyMode = false;
            return true;
        } else {
            index = prevIndex;
        }
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // TODO: Full rendering to be reimplemented using GL11
        // Original used RenderSystem, PoseStack, SuperRenderTypeBuffer, BoxElement
        // All from catnip/modern APIs not available in 1.7.10
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // TODO: renderSpeechBox - BoxElement/Pointing from catnip not available
    // public static void renderSpeechBox(...) { ... }
    public static void renderSpeechBox(int x, int y, int w, int h, boolean highlighted, int pointing, boolean returnWithLocalTransform) {
        // TODO: Reimplement using GL11
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public void coolDownAfterSkip() {
        skipCooling = 15;
    }

    public boolean isComfyReadingEnabled() {
        return PonderConfig.client().comfyReading;
    }

    public void setComfyReadingEnabled(boolean slowTextMode) {
        PonderConfig.client().comfyReading = slowTextMode;
    }

    public static float getPartialTicks() {
        // TODO: AnimationTickHolder from catnip not available
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof PonderUI ui) {
            if (ui.identifyMode)
                return ponderPartialTicksPaused;
        }
        return mc.timer.renderPartialTicks;
    }

    public ItemStack getSubject() {
        return null; // TODO
    }
}
