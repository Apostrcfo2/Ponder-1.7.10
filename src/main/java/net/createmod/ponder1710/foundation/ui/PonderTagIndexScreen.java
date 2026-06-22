package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.createmod.metanip.gui.NavigatableSimiScreen;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.createmod.metanip.layout.LayoutHelper;
import net.createmod.metanip.layout.PaginationState;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderTagIndexScreen extends AbstractPonderScreen {

    protected List<ModTagsEntry> currentModTagEntries = new LinkedList<>();
    protected List<Map.Entry<String, List<PonderTag>>> sortedModTags = new ArrayList<>();
    protected PaginationState paginationState = new PaginationState();

    @Nullable protected PonderButton pageNext;
    @Nullable protected PonderButton pagePrev;
    @Nullable private PonderTag hoveredItem = null;

    public PonderTagIndexScreen() {}

    @Override
    public void initGui() {
        super.initGui();

        Map<String, List<PonderTag>> tagsByMod = PonderIndex.getTagAccess().getListedTags()
            .stream().collect(Collectors.groupingBy(tag -> tag.getId().getResourceDomain()));
        sortedModTags = new TreeMap<>(tagsByMod).entrySet().stream().collect(Collectors.toList());

        int modCount = sortedModTags.size();
        int maxModsOnScreen = Math.max(1, (height - 140 - 40) / 58);
        paginationState = new PaginationState(modCount > 1 && modCount > maxModsOnScreen, maxModsOnScreen, modCount);

        setupModTagEntries();

        if (!paginationState.usesPagination()) return;

        int xOffset = width / 2;

        pagePrev = new PonderButton(xOffset - 120, height - 32).showing(PonderGuiTextures.ICON_PONDER_LEFT);
        pagePrev.withCallback(() -> { paginationState.previousPage(); updateAfterPaginationChange(); }).setActive(false);
        buttonList.add(pagePrev);
        pagePrev.updateGradientFromState();

        pageNext = new PonderButton(xOffset + 100, height - 32).showing(PonderGuiTextures.ICON_PONDER_RIGHT);
        pageNext.withCallback(() -> { paginationState.nextPage(); updateAfterPaginationChange(); }).setActive(true);
        buttonList.add(pageNext);
    }

    protected void setupModTagEntries() {
        // Remove tag buttons
        buttonList.removeIf(obj -> obj instanceof PonderButton && ((PonderButton) obj).getTag() != null);
        currentModTagEntries.clear();

        AtomicInteger yOffset = new AtomicInteger(140);
        int xOffset = width / 2;

        paginationState.iterateForCurrentPage((iPage, iOverall) -> {
            Map.Entry<String, List<PonderTag>> entry = sortedModTags.get(iOverall);
            String modId = entry.getKey();
            String modName = modId;
            ModContainer mod = Loader.instance().getIndexedModList().get(modId);
            if (mod != null) modName = mod.getName();

            List<PonderTag> tags = entry.getValue();
            LayoutHelper layout = LayoutHelper.centeredHorizontal(tags.size(), 1, 28, 28, 8);
            int[] layoutArea = layout.getArea(); // {x, y, w, h}

            for (PonderTag tag : tags) {
                PonderButton button = new PonderButton(xOffset + layout.getX() + 4, yOffset.get() + layout.getY() + 18)
                    .showingTag(tag);
                final PonderTag finalTag = tag;
                button.withCallback((mx, my) -> {
                    centerScalingOn(mx, my);
                    ScreenOpener.transitionTo(new PonderTagScreen(finalTag));
                });
                buttonList.add(button);
                layout.next();
            }

            currentModTagEntries.add(new ModTagsEntry(modName, tags.size(), layoutArea, yOffset.get()));
            yOffset.addAndGet(68);
        });
    }

    protected void updateAfterPaginationChange() {
        setupModTagEntries();
        if (pagePrev != null) pagePrev.setActive(paginationState.hasPreviousPage()).animateGradientFromState();
        if (pageNext != null) pageNext.setActive(paginationState.hasNextPage()).animateGradientFromState();
    }

    @Override
    protected void initBackTrackIcon(BoxWidget backTrack) {
        backTrack.showing(PonderGuiTextures.ICON_PONDER_IDENTIFY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        PonderUI.ponderTicks++;

        hoveredItem = null;
        for (Object obj : buttonList) {
            if (!(obj instanceof PonderButton)) continue;
            PonderButton b = (PonderButton) obj;
            if (b == backTrack) continue;
            if (b.isHoveredOrFocused()) hoveredItem = b.getTag();
        }
    }

    @Override
    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(mouseX, mouseY, partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslatef(width / 2f, 30, 0);

        // Title + logo
        GL11.glPushMatrix();
        GL11.glTranslatef(-120, 0, 0);
        String title = Ponder.lang().translate(AbstractPonderScreen.WELCOME).string();
        PonderGuiTextures.LOGO.render(-1, -1);
        GL11.glTranslatef(34, -3, 0);
        UIRenderHelper.streak(0, 0, 18, 36, 280);
        GL11.glScalef(2f, 2f, 2f);
        drawString(mc.fontRendererObj, title, 3, 5, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();

        GL11.glTranslatef(0, 50, 0);

        // Description
        String desc = Ponder.lang().translate(AbstractPonderScreen.DESCRIPTION).string();
        int maxWidth = Math.min((int)(width * .5f), mc.fontRendererObj.getStringWidth(desc) + 2);
        GL11.glTranslatef(-maxWidth / 2f, 0, 0);
        mc.fontRendererObj.drawSplitString(desc, 0, 0, maxWidth, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glTranslatef(maxWidth / 2f, 0, 0);

        GL11.glTranslatef(0, -80, 0);

        // Mod tag entries
        for (ModTagsEntry entry : currentModTagEntries) {
            GL11.glPushMatrix();
            renderTagsEntry(entry);
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    protected void renderTagsEntry(ModTagsEntry entry) {
        GL11.glTranslatef(0, entry.yPos, 0);

        // Category label
        String categories = Ponder.lang().translate(AbstractPonderScreen.CATEGORIES, entry.modName).string();
        int strW = mc.fontRendererObj.getStringWidth(categories);
        GL11.glPushMatrix();
        GL11.glTranslatef(-strW / 2f, -20, 0);
        drawString(mc.fontRendererObj, categories, 0, 0, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();

        // Streak background
        int lh = entry.layoutArea[3];
        int lw = entry.layoutArea[2];
        int extra = MathHelper.clamp_int(entry.tagCount, 2, 8);
        UIRenderHelper.streak(0, 0, lh / 2, lh + 6, lw / 2 + extra * 15);
        UIRenderHelper.streak(180, 0, lh / 2, lh + 6, lw / 2 + extra * 15);
    }

    @Override
    protected void renderWindowForeground(int mouseX, int mouseY, float partialTicks) {
        if (hoveredItem == null) return;
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 200);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // Render tag tooltip: title + description
        List<String> lines = new ArrayList<>();
        lines.add(hoveredItem.getTitle());
        lines.add(hoveredItem.getDescription());
        drawHoveringText(lines, mouseX, mouseY, mc.fontRendererObj);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    @Override
    public boolean isEquivalentTo(NavigatableSimiScreen other) {
        return other instanceof PonderTagIndexScreen;
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public static class ModTagsEntry {
        public final String modName;
        public final int tagCount;
        public final int[] layoutArea; // {x, y, w, h}
        public final int yPos;

        public ModTagsEntry(String modName, int tagCount, int[] layoutArea, int yPos) {
            this.modName = modName;
            this.tagCount = tagCount;
            this.layoutArea = layoutArea;
            this.yPos = yPos;
        }
    }
}
