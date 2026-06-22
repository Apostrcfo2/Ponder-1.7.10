package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.NavigatableSimiScreen;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.createmod.metanip.layout.LayoutHelper;
import net.createmod.metanip.registry.RegisteredObjectsHelper;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderTagScreen extends AbstractPonderScreen {

    private final PonderTag tag;
    protected final List<ItemEntry> items = new ArrayList<>();
    private final double itemXmult = 0.5;
    private final double mainYmult = 0.15;
    // int[] {x, y, w, h} replaces Rect2i
    @Nullable protected int[] itemArea;

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

        PonderIndex.getTagAccess().getItems(tag).stream()
            .map(key -> {
                Object obj = RegisteredObjectsHelper.getItemOrBlock(key);
                return obj instanceof Item ? new ItemEntry((Item) obj, key) : null;
            })
            .filter(e -> e != null)
            .forEach(items::add);

        // Remove main item from list (shown separately)
        ItemStack mainItem = tag.getMainItem();
        if (mainItem != null && mainItem.getItem() != null)
            items.removeIf(e -> e.item == mainItem.getItem());

        int rowCount = MathHelper.clamp_int((int) Math.ceil(items.size() / 11d), 1, 3);
        LayoutHelper layout = LayoutHelper.centeredHorizontal(items.size(), rowCount, 28, 28, 8);
        itemArea = layout.getArea();

        int itemCenterX = (int)(width * itemXmult);
        int itemCenterY = getItemsY();

        for (ItemEntry entry : items) {
            PonderButton b = new PonderButton(itemCenterX + layout.getX() + 4, itemCenterY + layout.getY() + 4)
                .showing(new ItemStack(entry.item));

            if (PonderIndex.getSceneAccess().doScenesExistForId(entry.key)) {
                b.withCallback((mx, my) -> {
                    centerScalingOn(mx, my);
                    ScreenOpener.transitionTo(PonderUI.of(new ItemStack(entry.item), tag));
                });
            } else {
                String ns = entry.key.getResourceDomain();
                b.withBorderColors(ns.equals("minecraft") ? PonderUI.MISSING_VANILLA_ENTRY : PonderUI.MISSING_MODDED_ENTRY)
                 .animateColors(false);
            }
            buttonList.add(b);
            layout.next();
        }

        // Main item button
        if (mainItem != null && mainItem.getItem() != null) {
            ResourceLocation mainKey = RegisteredObjectsHelper.getKeyOrThrow(mainItem.getItem());
            PonderButton mainBtn = new PonderButton(
                itemCenterX - layout.getTotalWidth() / 2 - 48, itemCenterY - 10)
                .showing(mainItem);
            if (PonderIndex.getSceneAccess().doScenesExistForId(mainKey)) {
                mainBtn.withCallback((mx, my) -> {
                    centerScalingOn(mx, my);
                    ScreenOpener.transitionTo(PonderUI.of(mainItem, tag));
                });
            } else {
                String ns = mainKey.getResourceDomain();
                mainBtn.withBorderColors(ns.equals("minecraft") ? PonderUI.MISSING_VANILLA_ENTRY : PonderUI.MISSING_MODDED_ENTRY)
                       .animateColors(false);
            }
            buttonList.add(mainBtn);
        }
    }

    @Override
    protected void initBackTrackIcon(BoxWidget backTrack) {
        // Show the tag icon on the back button
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
            if (b.isHoveredOrFocused() && b.getItem() != null)
                hoveredItem = b.getItem();
        }
    }

    @Override
    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(mouseX, mouseY, partialTicks);

        // Tag header (icon + title)
        GL11.glPushMatrix();
        GL11.glTranslatef(width / 2f - 120, (float)(height * mainYmult) - 40, 0);

        int x = 31 + 20 + 8;
        int y = 31;
        String title = tag.getTitle();

        UIRenderHelper.streak(0, x - 4, y - 12 + 17, 35, 240);

        // Tag icon (scaled)
        GL11.glPushMatrix();
        GL11.glTranslatef(23, 23, 10);
        GL11.glScalef(1.66f, 1.66f, 1.66f);
        tag.render(0, 0);
        GL11.glPopMatrix();

        drawString(mc.fontRendererObj,
            Ponder.lang().translate(AbstractPonderScreen.PONDERING_TAG).string(),
            x, y - 6, UIRenderHelper.COLOR_TEXT_DARKER.getFirst().getRGB());
        drawString(mc.fontRendererObj, title, x, y + 2,
            UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

        GL11.glPopMatrix();

        // Items area
        renderItems(mouseX, mouseY, partialTicks);

        // Description box
        int descW = (int)(width * .45);
        int descX = (width - descW) / 2;
        int descY = getItemsY() - 10 + Math.max(itemArea != null ? itemArea[3] : 28, 48);
        String desc = tag.getDescription();
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 100);
        mc.fontRendererObj.drawSplitString(desc, descX, descY, descW,
            UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();
    }

    protected void renderItems(int mouseX, int mouseY, float partialTicks) {
        if (items.isEmpty() || itemArea == null) return;

        int cx = (int)(width * itemXmult);
        int cy = getItemsY();

        GL11.glPushMatrix();
        GL11.glTranslatef(cx, cy, 0);

        String relatedTitle = Ponder.lang().translate(AbstractPonderScreen.ASSOCIATED).string();
        int strW = mc.fontRendererObj.getStringWidth(relatedTitle);

        UIRenderHelper.streak(0, 0, 0, itemArea[3] + 10, itemArea[2] / 2 + 75);
        UIRenderHelper.streak(180, 0, 0, itemArea[3] + 10, itemArea[2] / 2 + 75);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 200);
        drawCenteredString(mc.fontRendererObj, relatedTitle, 0, itemArea[1] - 20,
            UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    @Override
    protected void renderWindowForeground(int mouseX, int mouseY, float partialTicks) {
        if (hoveredItem == null || hoveredItem.getItem() == null) return;
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 200);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        renderToolTip(hoveredItem, mouseX, mouseY);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    @Override
    protected String getBreadcrumbTitle() {
        return tag.getTitle();
    }

    @Override
    public boolean isEquivalentTo(NavigatableSimiScreen other) {
        if (other instanceof PonderTagScreen)
            return tag == ((PonderTagScreen) other).tag;
        return super.isEquivalentTo(other);
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public int getItemsY() { return (int)(mainYmult * height + 85); }

    public PonderTag getTag() { return tag; }

    public static class ItemEntry {
        @Nullable public final Item item;
        public final ResourceLocation key;
        public ItemEntry(@Nullable Item item, ResourceLocation key) {
            this.item = item;
            this.key = key;
        }
    }
}
