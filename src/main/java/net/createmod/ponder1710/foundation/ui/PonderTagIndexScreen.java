package net.createmod.ponder1710.foundation.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.layout.LayoutHelper;
import net.createmod.metanip.layout.PaginationState;

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTag;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class PonderTagIndexScreen extends AbstractPonderScreen {

    protected List<ModTagsEntry> currentModTagEntries = new LinkedList<>();
    protected List<Map.Entry<String, List<PonderTag>>> sortedModTags = new ArrayList<>();
    protected int currentPage = 0;
    protected int maxModsOnScreen;

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

        maxModsOnScreen = Math.max(1, (height - 140 - 40) / 58);
        currentModTagEntries.clear();

        int yPos = 100;
        for (Map.Entry<String, List<PonderTag>> entry : sortedModTags) {
            String modId = entry.getKey();
            List<PonderTag> tags = entry.getValue();

            // Get mod display name via Forge loader
            String modName = modId;
            cpw.mods.fml.common.ModContainer mod =
                cpw.mods.fml.common.Loader.instance().getIndexedModList().get(modId);
            if (mod != null) modName = mod.getName();

            int[] layout = LayoutHelper.calcSpacing(tags.size(), 5, 30);
            currentModTagEntries.add(new ModTagsEntry(modName, tags.size(), layout[0], layout[1], yPos));
            yPos += 58;
        }
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

        int x = width / 2;
        int y = 50;

        // Title
        drawCenteredString(mc.fontRendererObj,
            net.createmod.ponder1710.Ponder.lang().translate(AbstractPonderScreen.INDEX_TITLE).string(),
            x, y, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

        // Mod sections
        int startIndex = currentPage * maxModsOnScreen;
        int endIndex = Math.min(startIndex + maxModsOnScreen, sortedModTags.size());

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<String, List<PonderTag>> entry = sortedModTags.get(i);
            ModTagsEntry layoutEntry = currentModTagEntries.get(i);
            String modName = layoutEntry.modName;
            List<PonderTag> tags = entry.getValue();

            int entryY = layoutEntry.yPos - currentPage * maxModsOnScreen * 58;

            // Streak background
            UIRenderHelper.streak(x - 80, entryY + 19, 19, 38, 250);

            // Mod name
            drawString(mc.fontRendererObj, modName, x - 70, entryY + 15, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

            // Tag buttons
            int tagX = x - 70;
            for (PonderTag tag : tags) {
                tag.render(tagX, entryY + 26);
                tagX += 20;
            }
        }

        GL11.glPopMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }

    public static class ModTagsEntry {
        public final String modName;
        public final int tagCount;
        public final int layoutWidth;
        public final int layoutHeight;
        public final int yPos;

        public ModTagsEntry(String modName, int tagCount, int layoutWidth, int layoutHeight, int yPos) {
            this.modName = modName; this.tagCount = tagCount;
            this.layoutWidth = layoutWidth; this.layoutHeight = layoutHeight;
            this.yPos = yPos;
        }
    }
}
