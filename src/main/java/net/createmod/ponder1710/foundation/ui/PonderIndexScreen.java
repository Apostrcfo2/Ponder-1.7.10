package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.platform.Window; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.metanip.gui.NavigatableSimiScreen; // TODO: catnip not available
// import net.createmod.metanip.gui.ScreenOpener; // TODO: catnip not available
// import net.createmod.metanip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.metanip.gui.widget.BoxWidget; // TODO: catnip not available
// import net.createmod.metanip.layout.LayoutHelper; // TODO: catnip not available
// import net.createmod.metanip.layout.PaginationState; // TODO: catnip not available
// import net.createmod.metanip.registry.RegisteredObjectsHelper; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.gui.components.events.GuiEventListener; // not available in 1.7.10
// import net.minecraft.client.renderer.Rect2i; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.level.ItemLike; // not available in 1.7.10

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.registration.PonderIndexExclusionHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class PonderIndexScreen extends AbstractPonderScreen {

    protected final List<ItemEntry> items;
    protected List<PonderButton> paginatedWidgets = new ArrayList<>();
    protected int maxItemRows;
    protected int maxItemsPerRow;
    protected int maxItemsPerPage;
    protected int currentPage = 0;

    @Nullable
    protected PonderButton nextPage;
    @Nullable
    protected PonderButton prevPage;

    @Nullable
    private ItemStack hoveredItem = null;

    // TODO: Predicate<ItemLike> -> Predicate<Item> in 1.7.10
    private final List<Predicate<Item>> exclusions;

    public PonderIndexScreen() {
        items = new ArrayList<>();
        exclusions = PonderIndex.streamPlugins()
            .flatMap(PonderIndexExclusionHelper::pluginToExclusions)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void initGui() {
        super.initGui();
        items.clear();

        // TODO: RegisteredObjectsHelper from catnip not available
        // TODO: PaginationState from catnip not available
        // TODO: LayoutHelper from catnip not available
        // Full init to be reimplemented using 1.7.10 item registry

        int targetWidth = MathHelper.clamp_int(width - 180, 250, 400);
        int targetHeight = MathHelper.clamp_int(height - 140, 150, 300);
        maxItemRows = (targetHeight + 8) / 36;
        maxItemsPerRow = (targetWidth + 8) / 36;
        maxItemsPerPage = maxItemRows * maxItemsPerRow;
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

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    // TODO: record not available in Java 8
    // public record ItemEntry(@Nullable ItemLike item, ResourceLocation key) {}
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
