package net.createmod.metanip.lang;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

// import org.joml.Matrix4f; // JOML available but not needed here
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.renderer.LightTexture; // not available in 1.7.10
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10
// import net.createmod.metanip.platform.CatnipClientServices; // using Locale.getDefault()

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class ClientFontHelper {

    public static List<String> cutString(FontRenderer font, String text, int maxWidthPerLine) {
        List<String> words = new LinkedList<>();
        BreakIterator iterator = BreakIterator.getLineInstance(Locale.getDefault());
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next())
            words.add(text.substring(start, end));

        List<String> lines = new LinkedList<>();
        StringBuilder currentLine = new StringBuilder();
        int width = 0;
        for (String word : words) {
            int newWidth = font.getStringWidth(word);
            if (width + newWidth > maxWidthPerLine) {
                if (width > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    width = 0;
                } else {
                    lines.add(word);
                    continue;
                }
            }
            currentLine.append(word);
            width += newWidth;
        }
        if (width > 0) lines.add(currentLine.toString());
        return lines;
    }

    public static void drawSplitString(FontRenderer font, String text, int x, int y, int width, int color) {
        List<String> lines = cutString(font, text, width);
        for (String line : lines) {
            font.drawString(line, x, y, color);
            y += 9;
        }
    }
}
