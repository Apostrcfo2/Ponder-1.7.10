package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.platform.Window; // not available in 1.7.10
// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.catnip.gui.NavigatableSimiScreen; // TODO: catnip not available
// import net.createmod.catnip.gui.ScreenOpener; // TODO: catnip not available
// import net.createmod.catnip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.catnip.gui.element.BoxElement; // TODO: catnip not available
// import net.createmod.catnip.gui.widget.BoxWidget; // TODO: catnip not available
// import net.createmod.catnip.lang.ClientFontHelper; // TODO: catnip not available
// import net.createmod.catnip.layout.LayoutHelper; // TODO: catnip not available
// import net.createmod.catnip.registry.RegisteredObjectsHelper; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.gui.components.events.GuiEventListener; // not available
// import net.minecraft.client.renderer.Rect2i; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.level.ItemLike; // not available in 1.7.10

import net.createmod.ponder1710.foundation.PonderChapter;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PonderTagScreen extends AbstractPonderScreen {

    private final PonderTag tag;
    protected final List<ItemEntry> items = new ArrayList<>();
    private final double itemXmult = 0.5;
    private final double mainYmult = 0.15;

    @Nullable
    private ItemStack hoveredItem = null;

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

        // TODO: RegisteredObjectsHelper from catnip not available
        // TODO: LayoutHelper from catnip not available
        // Full init to be reimplemented using 1.7.10 item registry
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        PonderUI.ponderTicks++;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // TODO: Full rendering to be reimplemented using GL11
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public int getItemsY() {
        return (int) (mainYmult * height + 85);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public PonderTag getTag() {
        return tag;
    }

    // TODO: record not available in Java 8
    public static class ItemEntry {
        @Nullable
        public final Item item;
        public final ResourceLocation key;

        public ItemEntry(@Nullable Item item, ResourceLocation key) {
            this.item = item;
            this.key = key;
        }
    }
}
