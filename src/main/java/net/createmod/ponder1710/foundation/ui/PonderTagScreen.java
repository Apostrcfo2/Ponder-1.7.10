package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.metanip.lang.ClientFontHelper;
import net.createmod.metanip.registry.RegisteredObjectsHelper;

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderTagScreen extends AbstractPonderScreen {

    private final PonderTag tag;
    protected final List<ItemEntry> items = new ArrayList<>();
    private final double itemXmult = 0.5;
    private final double mainYmult = 0.15;

    @Nullable private ItemStack hoveredItem = null;

    public PonderTagScreen(ResourceLocation tag) {
        this.tag = PonderIndex.getTagAccess().getRegisteredTag(tag);
    }

    public PonderTagScreen(PonderTag tag) {
        this.tag = tag;
    }

    @Override
    public void initGui() {
        super.initGui();
        items.clear();

        // Populate items that have scenes tagged with this tag
        PonderIndex.getSceneAccess().getRegisteredEntries().forEach(entry -> {
            ResourceLocation key = entry.getKey();
            List<net.createmod.ponder1710.foundation.PonderScene> scenes =
                PonderIndex.getSceneAccess().compile(key);
            boolean hasTag = scenes.stream().anyMatch(s -> s.getTags().contains(tag));
            if (!hasTag) return;

            Item item = (Item) RegisteredObjectsHelper.getItemOrBlock(key);
            if (item == null) return;
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

        int cx = width / 2;
        int headerY = (int)(mainYmult * height);

        // Tag icon + title
        tag.render(cx - 8, headerY);
        drawCenteredString(mc.fontRendererObj, tag.getTitle(), cx, headerY + 20,
            UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        drawCenteredString(mc.fontRendererObj, tag.getDescription(), cx, headerY + 32,
            UIRenderHelper.COLOR_TEXT_DARKER.getFirst().getRGB());

        // Items
        int itemsY = getItemsY();
        int itemsX = (int)(itemXmult * width);
        int startX = itemsX - items.size() * 18;

        for (int i = 0; i < items.size(); i++) {
            ItemEntry entry = items.get(i);
            int ix = startX + i * 36;
            int iy = itemsY;

            if (entry.item != null) {
                ItemStack stack = new ItemStack(entry.item);
                GuiGameElement.of(stack).at(ix, iy).render();

                boolean hovered = mouseX >= ix && mouseX <= ix+16 && mouseY >= iy && mouseY <= iy+16;
                if (hovered) {
                    hoveredItem = stack;
                }
            }
        }

        if (hoveredItem != null)
            renderToolTip(hoveredItem, mouseX, mouseY);

        GL11.glPopMatrix();
    }

    public int getItemsY() { return (int)(mainYmult * height + 85); }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public PonderTag getTag() { return tag; }

    public static class ItemEntry {
        @Nullable public final Item item;
        public final ResourceLocation key;

        public ItemEntry(@Nullable Item item, ResourceLocation key) {
            this.item = item; this.key = key;
        }
    }
}
