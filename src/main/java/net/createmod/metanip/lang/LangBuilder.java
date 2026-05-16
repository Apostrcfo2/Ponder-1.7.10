package net.createmod.metanip.lang;

import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

// Removed: Component, MutableComponent, Style, RegistryAccess (not available in 1.7.10)
// Removed: Player.displayClientMessage (different in 1.7.10)
// Strings are plain String in 1.7.10

public class LangBuilder {

    String namespace;
    @Nullable
    StringBuilder builder;

    public LangBuilder(String namespace) {
        this.namespace = namespace;
    }

    public LangBuilder space() { return text(" "); }

    public LangBuilder newLine() { return text("\n"); }

    public LangBuilder translate(String langKey, Object... args) {
        return add(StatCollector.translateToLocalFormatted(namespace + "." + langKey, resolveBuilders(args)));
    }

    public LangBuilder text(String literalText) {
        return add(literalText);
    }

    public LangBuilder text(EnumChatFormatting format, String literalText) {
        return add(format + literalText);
    }

    public LangBuilder text(int color, String literalText) {
        // Color codes not directly applicable - use plain text
        return add(literalText);
    }

    public LangBuilder add(LangBuilder otherBuilder) {
        return add(otherBuilder.string());
    }

    public LangBuilder add(String text) {
        if (builder == null) builder = new StringBuilder();
        builder.append(text);
        return this;
    }

    public LangBuilder style(EnumChatFormatting format) {
        assertBuilder();
        String current = builder.toString();
        builder = new StringBuilder(format + current);
        return this;
    }

    public LangBuilder color(int color) {
        // Color codes not directly applicable in 1.7.10 chat
        return this;
    }

    public LangBuilder color(Color color) {
        return this;
    }

    public String string() {
        assertBuilder();
        return builder.toString();
    }

    public void addTo(List<? super String> tooltip) {
        tooltip.add(string());
    }

    private void assertBuilder() {
        if (builder == null)
            throw new IllegalStateException("No text was added to builder");
    }

    public static Object[] resolveBuilders(Object[] args) {
        for (int i = 0; i < args.length; i++)
            if (args[i] instanceof LangBuilder cb)
                args[i] = cb.string();
        return args;
    }

    public static final float DEFAULT_SPACE_WIDTH = 4.0F;

    static int getIndents(int defaultIndents) {
        int spaceWidth = Minecraft.getMinecraft().fontRenderer.getCharWidth(' ');
        if (DEFAULT_SPACE_WIDTH == spaceWidth) return defaultIndents;
        return MathHelper.ceiling_float_int(DEFAULT_SPACE_WIDTH * defaultIndents / spaceWidth);
    }
}
