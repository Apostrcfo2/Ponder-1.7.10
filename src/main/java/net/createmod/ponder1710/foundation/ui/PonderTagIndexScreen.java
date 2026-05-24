package net.createmod.ponder1710.foundation.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.platform.Window; // not available in 1.7.10
// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.metanip.gui.ScreenOpener; // TODO: catnip not available
// import net.createmod.metanip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.metanip.gui.element.BoxElement; // TODO: catnip not available
// import net.createmod.metanip.gui.widget.BoxWidget; // TODO: catnip not available
// import net.createmod.metanip.lang.ClientFontHelper; // TODO: catnip not available
// import net.createmod.metanip.lang.FontHelper; // TODO: catnip not available
// import net.createmod.metanip.layout.LayoutHelper; // TODO: catnip not available
// import net.createmod.metanip.layout.PaginationState; // TODO: catnip not available
// import net.createmod.metanip.platform.CatnipServices; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.gui.components.events.GuiEventListener; // not available
// import net.minecraft.client.renderer.Rect2i; // not available in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;
import net.minecraft.client.Minecraft;

public class PonderTagIndexScreen extends AbstractPonderScreen {

    protected List<ModTagsEntry> currentModTagEntries = new LinkedList<>();
    protected List<Map.Entry<String, List<PonderTag>>> sortedModTags = new java.util.ArrayList<>();
    protected int currentPage = 0;
    protected int maxModsOnScreen;

    @Nullable
    protected PonderButton pageNext;
    @Nullable
    protected PonderButton pagePrev;

    @Nullable
    private PonderTag hoveredItem = null;

    public PonderTagIndexScreen() {
    }

    @Override
    public void initGui() {
        super.initGui();

        Map<String, List<PonderTag>> tagsByModID = PonderIndex.getTagAccess().getListedTags().stream()
            // ResourceLocation.getNamespace() -> getResourceDomain() in 1.7.10
            .collect(Collectors.groupingBy(tag -> tag.getId().getResourceDomain()));
        sortedModTags = new TreeMap<>(tagsByModID).entrySet().stream().collect(Collectors.toList());

        maxModsOnScreen = (height - 140 - 40) / 58;

        // TODO: PaginationState from catnip not available
        // TODO: LayoutHelper from catnip not available
        // TODO: CatnipServices.PLATFORM.getModDisplayName not available
        // Full init to be reimplemented
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
    public static class ModTagsEntry {
        public final String modName;
        public final int tagCount;
        public final int layoutWidth;
        public final int layoutHeight;
        public final int yPos;

        public ModTagsEntry(String modName, int tagCount, int layoutWidth, int layoutHeight, int yPos) {
            this.modName = modName;
            this.tagCount = tagCount;
            this.layoutWidth = layoutWidth;
            this.layoutHeight = layoutHeight;
            this.yPos = yPos;
        }
    }
}
