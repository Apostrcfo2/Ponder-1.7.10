package net.createmod.ponder1710.foundation.ui;

import java.util.function.BiConsumer;

// import net.createmod.catnip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.catnip.gui.widget.AbstractSimiWidget; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10

import net.createmod.ponder1710.foundation.PonderChapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class ChapterLabel extends GuiButton {

    private final PonderChapter chapter;
    private final PonderButton button;

    public ChapterLabel(PonderChapter chapter, int x, int y, BiConsumer<Integer, Integer> onClick) {
        super(0, x, y, 175, 38, "");
        this.button = new PonderButton(x + 4, y + 4, 30, 30);
        this.chapter = chapter;
    }

    // TODO: render - GuiGraphics/UIRenderHelper not available in 1.7.10
    public void render(int mouseX, int mouseY, float partialTicks) {
        // TODO: UIRenderHelper.streak not available
        Minecraft.getMinecraft().fontRenderer.drawString(chapter.getTitle(), xPosition + 50, yPosition + 20, 0xFFFFFF);
    }
}
