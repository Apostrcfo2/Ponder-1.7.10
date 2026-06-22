package net.createmod.ponder1710.foundation.ui;

import java.util.function.BiConsumer;

import net.createmod.metanip.gui.UIRenderHelper;

import net.createmod.ponder1710.foundation.PonderChapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

// AbstractSimiWidget -> GuiButton in 1.7.10
// GuiGraphics -> GL11 direct
public class ChapterLabel extends GuiButton {

    private final PonderChapter chapter;
    private final PonderButton button;

    public ChapterLabel(PonderChapter chapter, int x, int y, BiConsumer<Integer, Integer> onClick) {
        super(0, x, y, 175, 38, "");
        this.chapter = chapter;
        this.button = new PonderButton(x + 4, y + 4, 30, 30);
        button.withCallback(onClick);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        isHovered = mouseX >= xPosition && mouseY >= yPosition
            && mouseX < xPosition + width && mouseY < yPosition + height;
        UIRenderHelper.streak(0, xPosition, yPosition + height / 2, height - 2, width);
        chapter.render(xPosition + 4, yPosition + 4);
        mc.fontRendererObj.drawString(chapter.getTitle(),
            xPosition + 50, yPosition + 14, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());
        mc.fontRendererObj.drawString(chapter.getDescription(),
            xPosition + 50, yPosition + 24, UIRenderHelper.COLOR_TEXT_DARKER.getFirst().getRGB());
        button.doRender(mouseX, mouseY, 0);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (!enabled || !visible) return false;
        if (button.mousePressed(mc, mouseX, mouseY)) return true;
        return false;
    }
}
