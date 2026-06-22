package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.animation.LerpedFloat.Chaser;
import net.createmod.metanip.gui.NavigatableSimiScreen;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.BoxElement;
import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.createmod.metanip.lang.ClientFontHelper;
import net.createmod.metanip.math.Pointing;
import net.createmod.metanip.registry.RegisteredObjectsHelper;
import net.createmod.metanip.theme.Color;

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
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class PonderUI extends AbstractPonderScreen {

    public static int ponderTicks;
    public static float ponderPartialTicksPaused;

    public static final Color BACKGROUND_TRANSPARENT = new Color(0xdd000000, true);
    public static final Color BACKGROUND_FLAT        = new Color(0xff000000, true);
    public static final Color BACKGROUND_IMPORTANT   = new Color(0xdd0e0e20, true);
    public static final net.createmod.metanip.data.Couple<Color> COLOR_IDLE = net.createmod.metanip.data.Couple.create(
        new Color(0x40_ffeedd, true), new Color(0x20_ffeedd, true)
    ).map(Color::setImmutable);
    public static final net.createmod.metanip.data.Couple<Color> COLOR_HOVER = net.createmod.metanip.data.Couple.create(
        new Color(0x70_ffffff, true), new Color(0x30_ffffff, true)
    ).map(Color::setImmutable);
    public static final net.createmod.metanip.data.Couple<Color> COLOR_HIGHLIGHT = net.createmod.metanip.data.Couple.create(
        new Color(0xf0_ffeedd, true), new Color(0x60_ffeedd, true)
    ).map(Color::setImmutable);
    public static final net.createmod.metanip.data.Couple<Color> MISSING_VANILLA_ENTRY = net.createmod.metanip.data.Couple.create(
        new Color(0x50_5000ff, true), new Color(0x50_28007f, true)
    ).map(Color::setImmutable);
    public static final net.createmod.metanip.data.Couple<Color> MISSING_MODDED_ENTRY = net.createmod.metanip.data.Couple.create(
        new Color(0x70_984500, true), new Color(0x70_692400, true)
    ).map(Color::setImmutable);

    private final List<PonderScene> scenes;
    private final List<PonderTag>   tags;
    private List<PonderButton>      tagButtons = new ArrayList<>();
    private List<LerpedFloat>       tagFades   = new ArrayList<>();

    @Nullable PonderChapter chapter = null;

    private boolean   userViewMode;
    private boolean   identifyMode;
    @Nullable private ItemStack hoveredTooltipItem = null;
    @Nullable private int[]     hoveredBlockPos;
    @Nullable private int[]     copiedBlockPos;

    private final LerpedFloat fadeIn;
    private final LerpedFloat finishingFlash;
    private final LerpedFloat nextUp;
    private final LerpedFloat lazyIndex;
    private int finishingFlashWarmup = 0;
    private int nextUpWarmup = 0;

    private int index = 0;
    @Nullable private PonderTag referredToByTag;

    private PonderButton left, right, scan, close, replay, slowMode;
    @Nullable private PonderButton userMode;
    private int skipCooling = 0;
    private int extendedTickLength = 0;
    private int extendedTickTimer  = 0;

    ItemStack stack;

    public static PonderUI of(ResourceLocation id) {
        return new PonderUI(PonderIndex.getSceneAccess().compile(id));
    }

    public static PonderUI of(ItemStack item) {
        return new PonderUI(PonderIndex.getSceneAccess().compile(
            RegisteredObjectsHelper.getKeyOrThrow(item.getItem())));
    }

    public static PonderUI of(ItemStack item, PonderTag tag) {
        PonderUI ui = of(item);
        ui.referredToByTag = tag;
        return ui;
    }

    protected PonderUI(List<PonderScene> scenes) {
        if (!scenes.isEmpty()) {
            ResourceLocation loc = scenes.get(0).getLocation();
            stack = new ItemStack(RegisteredObjectsHelper.getItemOrBlock(loc));
            tags  = new ArrayList<>(PonderIndex.getTagAccess().getTags(loc));
        } else {
            stack = null;
            tags  = new ArrayList<>();
        }

        List<PonderScene> ordered;
        try {
            ordered = orderScenes(scenes);
        } catch (Exception e) {
            Ponder.LOGGER.warn("Unable to sort PonderScenes", e);
            ordered = scenes;
        }
        this.scenes = new ArrayList<>(ordered);

        if (this.scenes.isEmpty()) {
            List<StoryBoardEntry> list = Collections.singletonList(
                new PonderStoryBoardEntry(DebugScenes::empty, Ponder.MOD_ID,
                    "debug/scene_1", new ResourceLocation("stick")));
            this.scenes.addAll(PonderIndex.getSceneAccess().compile(list));
        }

        lazyIndex      = LerpedFloat.linear().startWithValue(index);
        fadeIn         = LerpedFloat.linear().startWithValue(0).chase(1, .1f, Chaser.EXP);
        finishingFlash = LerpedFloat.linear().startWithValue(0).chase(0, .1f, Chaser.EXP);
        nextUp         = LerpedFloat.linear().startWithValue(0).chase(0, .4f, Chaser.EXP);
    }

    private List<PonderScene> orderScenes(List<PonderScene> scenes) {
        // Topological sort using simple DFS (Guava MutableGraph not available)
        Map<Boolean, List<PonderScene>> partitioned = scenes.stream()
            .collect(Collectors.partitioningBy(s -> s.getOrderingEntries().isEmpty()));

        List<PonderScene> withOrder    = partitioned.get(false);
        List<PonderScene> withoutOrder = partitioned.get(true);
        if (withOrder.isEmpty()) return scenes;

        // Build adjacency manually
        Map<ResourceLocation, PonderScene> lookup = scenes.stream()
            .collect(Collectors.toMap(PonderScene::getId, s -> s));

        java.util.Map<PonderScene, List<PonderScene>> successors = new java.util.HashMap<>();
        scenes.forEach(s -> successors.put(s, new ArrayList<>()));

        for (int i = 1; i < withoutOrder.size(); i++)
            successors.get(withoutOrder.get(i-1)).add(withoutOrder.get(i));

        withOrder.forEach(scene ->
            scene.getOrderingEntries().forEach(entry -> {
                PonderScene other = lookup.get(entry.sceneId());
                if (other == null) return;
                if (entry.type() == SceneOrderingType.BEFORE)
                    successors.get(scene).add(other);
                else
                    successors.get(other).add(scene);
            })
        );

        List<PonderScene> result = new ArrayList<>();
        Set<PonderScene> visited = new HashSet<>();
        Set<PonderScene> visiting = new HashSet<>();
        for (PonderScene node : scenes)
            if (!visited.contains(node))
                dfs(node, successors, visited, visiting, result);

        Collections.reverse(result);
        return result;
    }

    private void dfs(PonderScene node, Map<PonderScene, List<PonderScene>> successors,
        Set<PonderScene> visited, Set<PonderScene> visiting, List<PonderScene> result) {
        if (visiting.contains(node) || visited.contains(node)) return;
        visiting.add(node);
        for (PonderScene next : successors.getOrDefault(node, Collections.emptyList()))
            dfs(next, successors, visited, visiting, result);
        visiting.remove(node);
        visited.add(node);
        result.add(node);
    }

    @Override
    protected void initBackTrackIcon(BoxWidget backTrack) {
        if (stack != null && stack.getItem() != null)
            backTrack.showing(stack);
    }

    @Override
    public void initGui() {
        super.initGui();
        tagButtons = new ArrayList<>();
        tagFades   = new ArrayList<>();

        tags.forEach(t -> {
            int i = tagButtons.size();
            int x = 31, y = 81 + i * 30;
            PonderButton b = new PonderButton(x, y).showing(t)
                .withCallback((mX, mY) -> {
                    centerScalingOn(mX, mY);
                    ScreenOpener.transitionTo(new PonderTagScreen(t));
                });
            tagButtons.add(b);
            tagFades.add(LerpedFloat.linear().startWithValue(0).chase(0, .05f, Chaser.EXP));
        });

        int spacing = 8;
        int bX = (width - 20) / 2 - (70 + 2 * spacing);
        int bY = height - 20 - 31;

        int pX = width / 2 - 110;
        int pY = bY + 20 + 4;
        int pW = width - 2 * pX;
        // PonderProgressBar would go here

        scan = new PonderButton(bX, bY)
            .showing(PonderGuiTextures.ICON_PONDER_IDENTIFY)
            .enableFade(0, 5)
            .withCallback(() -> {
                identifyMode = !identifyMode;
                if (!identifyMode) scenes.get(index).deselect();
                else ponderPartialTicksPaused = AnimationTickHolder.getPartialTicksUI();
            });

        slowMode = new PonderButton(width - 20 - 31, bY)
            .showing(PonderGuiTextures.ICON_PONDER_SLOW_MODE)
            .enableFade(0, 5)
            .withCallback(() -> setComfyReadingEnabled(!isComfyReadingEnabled()));

        if (PonderIndex.editingModeActive())
            userMode = new PonderButton(width - 50 - 31, bY)
                .showing(PonderGuiTextures.ICON_PONDER_USER_MODE)
                .enableFade(0, 5)
                .withCallback(() -> userViewMode = !userViewMode);

        bX += 50 + spacing;
        left = new PonderButton(bX, bY)
            .showing(PonderGuiTextures.ICON_PONDER_LEFT)
            .enableFade(0, 5)
            .withCallback(() -> scroll(false));

        bX += 20 + spacing;
        close = new PonderButton(bX, bY)
            .showing(PonderGuiTextures.ICON_PONDER_CLOSE)
            .enableFade(0, 5)
            .withCallback(this::onGuiClosed);

        bX += 20 + spacing;
        right = new PonderButton(bX, bY)
            .showing(PonderGuiTextures.ICON_PONDER_RIGHT)
            .enableFade(0, 5)
            .withCallback(() -> scroll(true));

        bX += 50 + spacing;
        replay = new PonderButton(bX, bY)
            .showing(PonderGuiTextures.ICON_PONDER_REPLAY)
            .enableFade(0, 5)
            .withCallback(this::replay);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (skipCooling > 0) skipCooling--;

        if (referredToByTag != null) {
            for (int i = 0; i < scenes.size(); i++) {
                if (!scenes.get(i).getTags().contains(referredToByTag)) continue;
                if (i == index) break;
                scenes.get(index).fadeOut();
                index = i;
                scenes.get(index).begin();
                lazyIndex.chase(index, 1/4f, Chaser.EXP);
                identifyMode = false;
                break;
            }
            referredToByTag = null;
        }

        lazyIndex.tickChaser();
        fadeIn.tickChaser();
        finishingFlash.tickChaser();
        nextUp.tickChaser();

        PonderScene activeScene = scenes.get(index);
        extendedTickLength = 0;
        if (isComfyReadingEnabled())
            activeScene.forEachVisible(TextWindowElement.class, twe -> extendedTickLength = 2);

        if (extendedTickTimer == 0) {
            if (!identifyMode) {
                ponderTicks++;
                if (skipCooling == 0) activeScene.tick();
                float lv = lazyIndex.getValue();
                if (Math.abs(lv - index) > 1/512f)
                    scenes.get(lv < index ? index-1 : index+1).tick();
            }
            extendedTickTimer = extendedTickLength;
        } else extendedTickTimer--;

        if (activeScene.getCurrentTime() == activeScene.getTotalTime() - 1) {
            finishingFlashWarmup = 30;
            nextUpWarmup = 50;
        }
        if (finishingFlashWarmup > 0) {
            finishingFlashWarmup--;
            if (finishingFlashWarmup == 0) finishingFlash.setValue(1);
        }
        if (nextUpWarmup > 0) {
            nextUpWarmup--;
            if (nextUpWarmup == 0) nextUp.updateChaseTarget(1);
        }

        updateIdentifiedItem(activeScene);
    }

    public void updateIdentifiedItem(PonderScene activeScene) {
        hoveredTooltipItem = null;
        hoveredBlockPos = null;
        if (!identifyMode) return;

        Minecraft mc = Minecraft.getMinecraft();
        double mouseX = mc.mouseHelper.mouseX();
        double mouseY = mc.mouseHelper.mouseY();
        SceneTransform t = activeScene.getTransform();
        Vec3 vec1 = t.screenToScene(mouseX, mouseY, 1000, 0);
        Vec3 vec2 = t.screenToScene(mouseX, mouseY, -100, 0);
        net.createmod.metanip.data.Pair<ItemStack, int[]> pair = activeScene.rayTraceScene(vec1, vec2);
        hoveredTooltipItem = pair.getFirst();
        hoveredBlockPos    = pair.getSecond();
    }

    public PonderScene getActiveScene() { return scenes.get(index); }

    public void seekToTime(int time) {
        if (getActiveScene().getCurrentTime() > time) replay();
        getActiveScene().seekToTime(time);
        if (time != 0) coolDownAfterSkip();
    }

    protected void replay() {
        identifyMode = false;
        scenes.get(index).begin();
    }

    protected boolean scroll(boolean forward) {
        int prev = index;
        index = MathHelper.clamp_int(forward ? index+1 : index-1, 0, scenes.size()-1);
        if (prev != index) {
            scenes.get(prev).fadeOut();
            scenes.get(index).begin();
            lazyIndex.chase(index, 1/4f, Chaser.EXP);
            identifyMode = false;
            return true;
        }
        index = prev;
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        partialTicks = getPartialTicks();

        GL11.glEnable(GL11.GL_BLEND);
        renderVisibleScenes(mouseX, mouseY, skipCooling > 0 ? 0 : identifyMode ? ponderPartialTicksPaused : partialTicks);
        renderWidgets(mouseX, mouseY, identifyMode ? ponderPartialTicksPaused : partialTicks);
    }

    protected void renderVisibleScenes(int mouseX, int mouseY, float pt) {
        renderScene(mouseX, mouseY, index, pt);
        float lv = lazyIndex.getValue(pt);
        if (Math.abs(lv - index) > 1/512f)
            renderScene(mouseX, mouseY, lv < index ? index-1 : index+1, pt);
    }

    protected void renderScene(int mouseX, int mouseY, int i, float pt) {
        PonderScene scene = scenes.get(i);
        float lv = lazyIndex.getValue(AnimationTickHolder.getPartialTicksUI());
        double diff  = i - lv;
        double slide = MathHelper.lerp((float)(diff*diff), 200, 600) * diff;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, -800);

        scene.getTransform().updateScreenParams(width, height, slide);
        scene.getTransform().apply(pt);
        scene.getTransform().updateSceneRVE(pt);
        scene.renderScene(pt);

        // Platform shadow
        if (!scene.shouldHidePlatformShadow()) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glTranslatef(scene.getBasePlateOffsetX(), 0, scene.getBasePlateOffsetZ());
            UIRenderHelper.flipForGuiRender();

            float flash = finishingFlash.getValue(pt) * .9f;
            float alpha = flash;
            flash *= flash; flash = ((flash*2)-1); flash *= flash; flash = 1-flash;

            for (int f = 0; f < 4; f++) {
                GL11.glTranslatef(scene.getBasePlateSize(), 0, 0);
                GL11.glTranslatef(0, 0, -1/1024f);
                if (flash > 0) {
                    GL11.glPushMatrix();
                    GL11.glScalef(1, .5f + flash*.75f, 1);
                    drawGradientRect(0, -1, -scene.getBasePlateSize(), 0, 0x00c6ffc9, (int)(0xaa_c6ffc9 * alpha));
                    GL11.glPopMatrix();
                }
                GL11.glTranslatef(0, 0, 2/1024f);
                drawGradientRect(0, 0, -scene.getBasePlateSize(), 4, 0x66000000, 0x00000000);
                GL11.glRotatef(-90, 0, 1, 0);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        GL11.glPopMatrix();

        // Scene overlay
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 100);
        renderOverlay(i, skipCooling > 0 ? 0 : identifyMode ? ponderPartialTicksPaused : pt);
        GL11.glPopMatrix();
    }

    protected void renderWidgets(int mouseX, int mouseY, float pt) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        float fade = fadeIn.getValue(pt);
        float lv   = lazyIndex.getValue(pt);
        float diff = lv - index;
        PonderScene activeScene = scenes.get(index);
        PonderScene nextScene   = scenes.size() > index+1 ? scenes.get(index+1) : null;

        int tooltipColor = UIRenderHelper.COLOR_TEXT_DARKER.getFirst().getRGB();
        renderSceneInformation(fade, Math.abs(diff), activeScene, tooltipColor);

        // Identify mode cursor tooltip
        if (identifyMode) {
            GL11.glPushMatrix();
            GL11.glTranslatef(mouseX, mouseY, 100);
            if (hoveredTooltipItem == null || hoveredTooltipItem.getItem() == null) {
                String text = Ponder.lang().translate(AbstractPonderScreen.IDENTIFY_MODE,
                    Minecraft.getMinecraft().gameSettings.keyDrop.getKeyDescription()).string();
                drawHoveringText(java.util.Collections.singletonList(text), 0, 0);
            } else {
                renderToolTip(hoveredTooltipItem, 0, 0);
            }
            if (hoveredBlockPos != null && PonderIndex.editingModeActive() && !userViewMode) {
                GL11.glTranslatef(0, -15, 0);
                drawString(mc.fontRendererObj,
                    hoveredBlockPos[0] + ", " + hoveredBlockPos[1] + ", " + hoveredBlockPos[2],
                    0, 0, 0xFFD700);
            }
            GL11.glPopMatrix();
            scan.flash();
        } else {
            scan.dim();
        }

        if (PonderIndex.editingModeActive() && userMode != null) {
            if (userViewMode) userMode.flash();
            else userMode.dim();
        }

        if (isComfyReadingEnabled()) slowMode.flash();
        else slowMode.dim();

        renderNextUp(pt, nextScene);

        // Update button fades
        for (PonderButton btn : getAllButtons()) btn.fade().startWithValue(fade);

        if (index == 0 || (index == 1 && lv < index))
            left.fade().startWithValue(lv);
        if (index == scenes.size()-1 || (index == scenes.size()-2 && lv > index))
            right.fade().startWithValue(scenes.size() - lv - 1);

        if (activeScene.isFinished()) right.flash();
        else { right.dim(); nextUp.updateChaseTarget(0); }

        // Nav arrows
        Color c1 = COLOR_NAV_ARROW.getFirst().setAlpha(0x40);
        Color c2 = COLOR_NAV_ARROW.getFirst().setAlpha(0x20);
        Color c3 = COLOR_NAV_ARROW.getFirst().setAlpha(0x10);
        UIRenderHelper.breadcrumbArrow(width/2-20, height-51, 0, 20, 20, 5, c1, c2);
        UIRenderHelper.breadcrumbArrow(width/2+20, height-51, 0, -20, 20, -5, c1, c2);
        UIRenderHelper.breadcrumbArrow(width/2-90, height-51, 0, 70, 20, 5, c1, c3);
        UIRenderHelper.breadcrumbArrow(width/2+90, height-51, 0, -70, 20, -5, c1, c3);

        // Tags
        List<PonderTag> sceneTags = activeScene.getTags();
        boolean highlightAll = sceneTags.stream().anyMatch(t -> t.getId() == PonderTag.Highlight.ALL);
        for (int ti = 0; ti < tagButtons.size(); ti++) {
            GL11.glPushMatrix();
            PonderTag tag       = tags.get(ti);
            LerpedFloat chase   = tagFades.get(ti);
            PonderButton button = tagButtons.get(ti);

            if (button.isMouseOver(mouseX, mouseY)) chase.updateChaseTarget(1);
            else chase.updateChaseTarget(0);
            chase.tickChaser();

            if (highlightAll || sceneTags.contains(tag)) button.flash();
            else button.dim();

            int tx = button.xPosition + button.width + 4;
            int ty = button.yPosition - 2;
            GL11.glTranslatef(tx, ty + 5*(1-fade), 800);

            float fadedWidth = 200 * chase.getValue(pt);
            UIRenderHelper.streak(0, 0, 12, 26, (int)fadedWidth);

            GL11.glScissor((int)(tx * mc.gameSettings.guiScale), 0,
                (int)(fadedWidth * mc.gameSettings.guiScale), height * mc.gameSettings.guiScale);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            drawString(mc.fontRendererObj, tag.getTitle(), 3, 8,
                UIRenderHelper.COLOR_TEXT_ACCENT.getFirst().getRGB());
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }

        renderHoverTooltips(tooltipColor);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void renderHoverTooltips(int tooltipColor) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 500);
        int ty = height - 16;
        if (scan.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.IDENTIFY).string(), scan.xPosition+10, ty, tooltipColor);
        if (index != 0 && left.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.PREVIOUS).string(), left.xPosition+10, ty, tooltipColor);
        if (close.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.CLOSE).string(), close.xPosition+10, ty, tooltipColor);
        if (index != scenes.size()-1 && right.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.NEXT).string(), right.xPosition+10, ty, tooltipColor);
        if (replay.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.REPLAY).string(), replay.xPosition+10, ty, tooltipColor);
        if (slowMode.isHovered())
            drawCenteredString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.SLOW_TEXT).string(), slowMode.xPosition+5, ty, tooltipColor);
        GL11.glPopMatrix();
    }

    private void renderNextUp(float pt, @Nullable PonderScene nextScene) {
        if (!getActiveScene().isFinished()) return;
        if (nextScene == null || !nextScene.isNextUpEnabled()) return;
        if (!(nextUp.getValue() > 1/16f)) return;

        GL11.glPushMatrix();
        GL11.glTranslatef(right.xPosition+10, right.yPosition - 6 + nextUp.getValue(pt)*5, 400);
        String nextUpStr  = Ponder.lang().translate(AbstractPonderScreen.NEXT_UP).string();
        String titleStr   = nextScene.getTitle();
        int boxW = Math.max(mc.fontRendererObj.getStringWidth(titleStr),
                            mc.fontRendererObj.getStringWidth(nextUpStr)) + 5;
        renderSpeechBox(0, 0, boxW, 20, right.isHovered(), Pointing.DOWN, false);
        GL11.glTranslatef(0, -29, 100);
        drawCenteredString(mc.fontRendererObj, nextUpStr,  0, 0,  UIRenderHelper.COLOR_TEXT_DARKER.getFirst().getRGB());
        drawCenteredString(mc.fontRendererObj, titleStr,   0, 10, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();
    }

    private void renderSceneInformation(float fade, float indexDiff, PonderScene activeScene, int tooltipColor) {
        String title = activeScene.getTitle();
        int maxW = 180;
        int titleW = Math.min(mc.fontRendererObj.getStringWidth(title), maxW);
        int streakH = 35;
        int streakW = 70 + titleW;

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 400);
        GL11.glTranslatef(55, 19, 0);

        UIRenderHelper.streak(0, 0, streakH/2, streakH, (int)(streakW*fade));
        UIRenderHelper.streak(180, 0, streakH/2, streakH, (int)(30*fade));

        new BoxElement().withBackground(BACKGROUND_FLAT)
            .gradientBorder(COLOR_IDLE)
            .at(-34, 2, 100).withBounds(30, 30).render();

        if (stack != null)
            GuiGameElement.of(stack).scale(2).at(-35, 1).render();

        GL11.glTranslatef(4, 6, 0);
        drawString(mc.fontRendererObj, Ponder.lang().translate(AbstractPonderScreen.PONDERING).string(), 0, 0, tooltipColor);
        GL11.glTranslatef(0, 14, 0);
        ClientFontHelper.drawSplitString(mc.fontRendererObj, title, 0, 0, maxW,
            UIRenderHelper.COLOR_TEXT.getFirst().scaleAlphaForText(fade).getRGB());
        GL11.glPopMatrix();
    }

    private void renderOverlay(int i, float pt) {
        if (identifyMode) return;
        GL11.glPushMatrix();
        scenes.get(i).renderOverlay(this, skipCooling > 0 ? 0 : pt);
        GL11.glPopMatrix();
    }

    public static void renderSpeechBox(int x, int y, int w, int h, boolean highlighted,
        Pointing pointing, boolean returnWithLocalTransform) {

        if (!returnWithLocalTransform) GL11.glPushMatrix();

        int boxX = x, boxY = y, divotX = x, divotY = y;
        int divotRotation = 0, divotSize = 8, distance = 1;
        int divotRadius = divotSize / 2;
        net.createmod.metanip.data.Couple<Color> borderColors =
            highlighted ? PonderButton.COLOR_HOVER : net.createmod.metanip.data.Couple.create(
                new Color(COLOR_IDLE, true), new Color(COLOR_IDLE, true));
        Color c;

        switch (pointing) {
            case LEFT:
                divotRotation = 90; boxX += divotSize+1+distance; boxY -= h/2;
                divotX += distance; divotY -= divotRadius;
                c = Color.mixColors(borderColors, 0.5f); break;
            case RIGHT:
                divotRotation = 270; boxX -= w+divotSize+1+distance; boxY -= h/2;
                divotX -= divotSize+distance; divotY -= divotRadius;
                c = Color.mixColors(borderColors, 0.5f); break;
            case UP:
                divotRotation = 180; boxX -= w/2; boxY += divotSize+1+distance;
                divotX -= divotRadius; divotY += distance;
                c = borderColors.getFirst(); break;
            default: // DOWN
                divotRotation = 0; boxX -= w/2; boxY -= h+divotSize+1+distance;
                divotX -= divotRadius; divotY -= divotSize+distance;
                c = borderColors.getSecond(); break;
        }

        new BoxElement().withBackground(BACKGROUND_FLAT)
            .gradientBorder(borderColors)
            .at(boxX, boxY, 100).withBounds(w, h).render();

        GL11.glPushMatrix();
        GL11.glTranslatef(divotX + divotRadius, divotY + divotRadius, 110);
        GL11.glRotatef(divotRotation, 0, 0, 1);
        GL11.glTranslatef(-divotRadius, -divotRadius, 0);
        PonderGuiTextures.SPEECH_TOOLTIP_BACKGROUND.render(0, 0);
        PonderGuiTextures.SPEECH_TOOLTIP_COLOR.render(0, 0, c);
        GL11.glPopMatrix();

        if (returnWithLocalTransform) {
            GL11.glTranslatef(boxX, boxY, 0);
            return;
        }
        GL11.glPopMatrix();
    }

    private List<PonderButton> getAllButtons() {
        List<PonderButton> all = new ArrayList<>(tagButtons);
        if (scan != null)     all.add(scan);
        if (slowMode != null) all.add(slowMode);
        if (userMode != null) all.add(userMode);
        if (left != null)     all.add(left);
        if (close != null)    all.add(close);
        if (right != null)    all.add(right);
        if (replay != null)   all.add(replay);
        return all;
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public void coolDownAfterSkip() { skipCooling = 15; }

    public boolean isComfyReadingEnabled() { return PonderConfig.client().comfyReading; }
    public void setComfyReadingEnabled(boolean v) { PonderConfig.client().comfyReading = v; }

    public static float getPartialTicks() {
        float pt = AnimationTickHolder.getPartialTicksUI();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof PonderUI ui) {
            if (ui.identifyMode) return ponderPartialTicksPaused;
            return (pt + (ui.extendedTickLength - ui.extendedTickTimer)) / (ui.extendedTickLength + 1);
        }
        return pt;
    }

    @Override
    protected String getBreadcrumbTitle() {
        if (chapter != null) return chapter.getTitle();
        return stack != null ? stack.getDisplayName() : "";
    }

    public ItemStack getSubject()       { return stack; }
    public ItemStack getHoveredTooltipItem() { return hoveredTooltipItem; }

    @Override
    public boolean isEquivalentTo(NavigatableSimiScreen other) {
        if (other instanceof PonderUI o)
            return stack != null && o.stack != null && stack.getItem() == o.stack.getItem();
        return super.isEquivalentTo(other);
    }

    // Helper: gradient rect in 1.7.10
    private void drawGradientRect(int x1, int y1, int x2, int y2, int col1, int col2) {
        float a1 = ((col1>>24)&0xFF)/255f, r1=((col1>>16)&0xFF)/255f, g1=((col1>>8)&0xFF)/255f, b1=(col1&0xFF)/255f;
        float a2 = ((col2>>24)&0xFF)/255f, r2=((col2>>16)&0xFF)/255f, g2=((col2>>8)&0xFF)/255f, b2=(col2&0xFF)/255f;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(r1,g1,b1,a1); tess.addVertex(x1,y1,0);
        tess.setColorRGBA_F(r2,g2,b2,a2); tess.addVertex(x1,y2,0);
        tess.setColorRGBA_F(r2,g2,b2,a2); tess.addVertex(x2,y2,0);
        tess.setColorRGBA_F(r1,g1,b1,a1); tess.addVertex(x2,y1,0);
        tess.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
