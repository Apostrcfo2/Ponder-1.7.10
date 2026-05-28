package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.metanip.registry.RegisteredObjectsHelper;

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.registration.PonderIndexExclusionHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderIndexScreen extends AbstractPonderScreen {

    protected final List<ItemEntry> items;
    protected List<PonderButton> paginatedWidgets = new ArrayList<>();
    protected int maxItemRows;
    protected int maxItemsPerRow;
    protected int maxItemsPerPage;
    protected int currentPage = 0;

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

        int targetWidth  = MathHelper.clamp_int(width - 180, 250, 400);
        int targetHeight = MathHelper.clamp_int(height - 140, 150, 300);
        maxItemRows      = Math.max(1, (targetHeight + 8) / 36);
        maxItemsPerRow   = Math.max(1, (targetWidth + 8) / 36);
        maxItemsPerPage  = maxItemRows * maxItemsPerRow;

        // Populate items from registered scenes
        PonderIndex.getSceneAccess().getRegisteredEntries().forEach(entry -> {
            ResourceLocation key = entry.getKey();
            Item item = (Item) RegisteredObjectsHelper.getItemOrBlock(key);
            if (item == null) return;
            if (exclusions.stream().anyMatch(pred -> pred.test(item))) return;
            if (items.stream().anyMatch(e -> e.key.equals(key))) return;
            items.add(new ItemEntry(item, key));
        });
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        PonderUI.ponderTicks++;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 400);

        int startX = (width - maxItemsPerRow * 36) / 2;
        int startY = 80;

        int startIndex = currentPage * maxItemsPerPage;
        int endIndex   = Math.min(startIndex + maxItemsPerPage, items.size());

        for (int i = startIndex; i < endIndex; i++) {
            ItemEntry entry = items.get(i);
            int col = (i - startIndex) % maxItemsPerRow;
            int row = (i - startIndex) / maxItemsPerRow;
            int ix  = startX + col * 36;
            int iy  = startY + row * 36;

            if (entry.item != null) {
                ItemStack stack = new ItemStack(entry.item);
                GuiGameElement.of(stack).at(ix, iy).render();

                boolean hovered = mouseX >= ix && mouseX <= ix+16 && mouseY >= iy && mouseY <= iy+16;
                if (hovered) {
                    hoveredItem = stack;
                    drawString(mc.fontRendererObj, entry.key.toString(), 10, height - 20, 0xAAAAAA);
                }
            }
        }

        if (hoveredItem != null)
            renderToolTip(hoveredItem, mouseX, mouseY);

        GL11.glPopMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public static class ItemEntry {
        @Nullable public final Item item;
        public final ResourceLocation key;

        public ItemEntry(@Nullable Item item, ResourceLocation key) {
            this.item = item; this.key = key;
        }
    }
}
