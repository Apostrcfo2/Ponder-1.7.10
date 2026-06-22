package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.NavigatableSimiScreen;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.createmod.metanip.layout.LayoutHelper;
import net.createmod.metanip.layout.PaginationState;
import net.createmod.metanip.registry.RegisteredObjectsHelper;

import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.registration.PonderIndexExclusionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class PonderIndexScreen extends AbstractPonderScreen {

    protected final List<ItemEntry> items;
    protected List<PonderButton> paginatedWidgets = new ArrayList<>();
    protected PaginationState paginationState = new PaginationState();
    // int[] {x, y, width, height} replaces Rect2i
    protected int[] maxScreenArea = {0, 0, 0, 0};
    protected int[] usedArea = {0, 0, 0, 0};
    protected int maxItemRows;
    protected int maxItemsPerRow;
    protected int maxItemsPerPage;

    @Nullable protected PonderButton nextPage;
    @Nullable protected PonderButton prevPage;
    @Nullable private ItemStack hoveredItem = null;

    private final List<Predicate<Item>> exclusions;

    public PonderIndexScreen() {
        items = new ArrayList<>();
        exclusions = PonderIndex.streamPlugins()
            .flatMap(PonderIndexExclusionHelper::pluginToExclusions)
            .collect(Collectors.toList());
    }

    @Override
    public void initGui() {
        super.initGui();
        items.clear();

        PonderIndex.getSceneAccess().getRegisteredEntries().stream()
            .map(e -> e.getKey())
            .distinct()
            .map(key -> {
                Object obj = RegisteredObjectsHelper.getItemOrBlock(key);
                return obj instanceof Item ? new ItemEntry((Item) obj, key) : null;
            })
            .filter(e -> e != null && isItemIncluded(e))
            .sorted(Comparator.comparing(e -> e.key.toString()))
            .forEach(items::add);

        int centerX = width / 2;
        int centerY = height / 2;
        int targetWidth  = MathHelper.clamp_int(width - 180, 250, 400);
        int targetHeight = MathHelper.clamp_int(height - 140, 150, 300);

        maxScreenArea = new int[]{
            centerX - targetWidth / 2,
            centerY - targetHeight / 2,
            targetWidth, targetHeight
        };

        maxItemRows     = Math.max(1, (maxScreenArea[3] + 8) / 36);
        maxItemsPerRow  = Math.max(1, (maxScreenArea[2] + 8) / 36);
        maxItemsPerPage = maxItemRows * maxItemsPerRow;

        paginationState = new PaginationState(items.size() > maxItemsPerPage, maxItemsPerPage, items.size());

        setupItemsForPage();

        if (!paginationState.usesPagination()) return;

        prevPage = new PonderButton(centerX - 100, maxScreenArea[1] + maxScreenArea[3] + 10)
            .showing(PonderGuiTextures.ICON_PONDER_LEFT);
        prevPage.withCallback(() -> {
            paginationState.previousPage();
            updateAfterPaginationChange();
        }).setActive(false);
        buttonList.add(prevPage);

        nextPage = new PonderButton(centerX + 80, maxScreenArea[1] + maxScreenArea[3] + 10)
            .showing(PonderGuiTextures.ICON_PONDER_RIGHT);
        nextPage.withCallback(() -> {
            paginationState.nextPage();
            updateAfterPaginationChange();
        }).setActive(true);
        buttonList.add(nextPage);

        prevPage.updateGradientFromState();
        nextPage.updateGradientFromState();
    }

    protected void setupItemsForPage() {
        paginatedWidgets.forEach(buttonList::remove);
        paginatedWidgets.clear();

        int itemCount    = paginationState.getCurrentPageElementCount();
        int actualRows   = MathHelper.clamp_int((int) Math.ceil((double) itemCount / maxItemsPerRow), 1, maxItemRows);
        LayoutHelper lay = LayoutHelper.centeredHorizontal(itemCount, actualRows, 28, 28, 8);
        usedArea = lay.getArea();

        int centerX = width / 2;
        int centerY = height / 2;

        paginationState.iterateForCurrentPage((iPage, iOverall) -> {
            ItemEntry entry = items.get(iOverall);
            int bx = centerX + lay.getX() + 4;
            int by = centerY + lay.getY() + 4;
            PonderButton b = new PonderButton(bx, by)
                .showing(new ItemStack(entry.item));
            b.withCallback((x, y) -> {
                if (!PonderIndex.getSceneAccess().doScenesExistForId(entry.key)) return;
                centerScalingOn(x, y);
                ScreenOpener.transitionTo(PonderUI.of(new ItemStack(entry.item)));
            });
            paginatedWidgets.add(b);
            buttonList.add(b);
            lay.next();
        });
    }

    protected void updateAfterPaginationChange() {
        setupItemsForPage();
        if (prevPage != null) prevPage.setActive(paginationState.hasPreviousPage()).animateGradientFromState();
        if (nextPage != null) nextPage.setActive(paginationState.hasNextPage()).animateGradientFromState();
    }

    @Override
    protected void initBackTrackIcon(BoxWidget backTrack) {
        backTrack.showing(PonderGuiTextures.ICON_PONDER_IDENTIFY);
    }

    private boolean isItemIncluded(ItemEntry entry) {
        return exclusions.stream().noneMatch(p -> p.test(entry.item));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        PonderUI.ponderTicks++;

        hoveredItem = null;
        for (Object obj : buttonList) {
            if (!(obj instanceof PonderButton)) continue;
            PonderButton b = (PonderButton) obj;
            if (b.isHoveredOrFocused() && b.getItem() != null)
                hoveredItem = b.getItem();
        }
    }

    @Override
    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(mouseX, mouseY, partialTicks);
        int centerX = width / 2;
        int centerY = height / 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(centerX, centerY, 0);
        UIRenderHelper.streak(0, usedArea[0] - 10, usedArea[1] - 20, 20, 220);
        drawString(mc.fontRendererObj, "Items to inspect",
            usedArea[0] - 5, usedArea[1] - 25, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();

        if (!paginationState.usesPagination()) return;

        GL11.glPushMatrix();
        GL11.glTranslatef(centerX, maxScreenArea[1] + maxScreenArea[3] + 14, 0);
        GL11.glScalef(1.5f, 1.5f, 1);
        String pageStr = "Page " + (paginationState.getPageIndex() + 1) + "/" + paginationState.getMaxPages();
        int strW = mc.fontRendererObj.getStringWidth(pageStr);
        UIRenderHelper.streak(0, 0, 4, 14, 85);
        UIRenderHelper.streak(180, 0, 4, 14, 85);
        drawString(mc.fontRendererObj, pageStr, -strW / 2, 0, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();
    }

    @Override
    protected void renderWindowForeground(int mouseX, int mouseY, float partialTicks) {
        if (hoveredItem == null || hoveredItem.getItem() == null) return;
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 200);
        renderToolTip(hoveredItem, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Override
    public boolean isEquivalentTo(NavigatableSimiScreen other) {
        return other instanceof PonderIndexScreen;
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public static class ItemEntry {
        @Nullable public final Item item;
        public final ResourceLocation key;
        public ItemEntry(@Nullable Item item, ResourceLocation key) {
            this.item = item;
            this.key  = key;
        }
    }
}
