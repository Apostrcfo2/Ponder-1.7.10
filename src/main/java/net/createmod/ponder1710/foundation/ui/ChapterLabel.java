package net.createmod.ponder1710.foundation.ui;

import java.util.function.BiConsumer;

import net.createmod.metanip.gui.UIRenderHelper;

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

    public void render(int mouseX, int mouseY, float partialTicks) {
        UIRenderHelper.streak(xPosition, yPosition + 19, 19, 38, 175);
        chapter.render(xPosition + 4, yPosition + 4);
        Minecraft.getMinecraft().fontRendererObj.drawString(
            chapter.getTitle(), xPosition + 50, yPosition + 14, 0xFFFFFF);
        Minecraft.getMinecraft().fontRendererObj.drawString(
            chapter.getDescription(), xPosition + 50, yPosition + 24, 0xAAAAAA);
    }
}
