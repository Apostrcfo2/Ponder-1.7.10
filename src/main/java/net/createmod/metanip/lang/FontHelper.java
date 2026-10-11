package net.createmod.metanip.lang;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;

import net.createmod.metanip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

// Removed: Component, MutableComponent, Style, Font (using FontRenderer in 1.7.10)
// Removed: CatnipClientServices (using Locale.getDefault())

public final class FontHelper {

    public static final int MAX_WIDTH_PER_LINE = 200;

    private FontHelper() {}

    public static List<String> cutStringTextComponent(String s, Palette palette) {
        return cutString(s, MAX_WIDTH_PER_LINE);
    }

    public static List<String> cutString(String text, int maxWidth) {
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
            int newWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(word.replaceAll("_", ""));
            if (width + newWidth > maxWidth) {
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

    // TODO: record not available in Java 8
    public static class Palette {
        public final EnumChatFormatting primary;
        public final EnumChatFormatting highlight;

        public static final Palette ALL_GRAY = new Palette(EnumChatFormatting.GRAY, EnumChatFormatting.GRAY);
        public static final Palette GRAY = new Palette(EnumChatFormatting.DARK_GRAY, EnumChatFormatting.GRAY);
        public static final Palette BLUE = new Palette(EnumChatFormatting.BLUE, EnumChatFormatting.AQUA);
        public static final Palette GREEN = new Palette(EnumChatFormatting.DARK_GREEN, EnumChatFormatting.GREEN);
        public static final Palette YELLOW = new Palette(EnumChatFormatting.GOLD, EnumChatFormatting.YELLOW);
        public static final Palette RED = new Palette(EnumChatFormatting.DARK_RED, EnumChatFormatting.RED);

        public Palette(EnumChatFormatting primary, EnumChatFormatting highlight) {
            this.primary = primary;
            this.highlight = highlight;
        }

        public EnumChatFormatting primary() { return primary; }
        public EnumChatFormatting highlight() { return highlight; }
    }
}
