package net.createmod.metanip.render;

import net.createmod.metanip.theme.Color;

// GuiGraphics not available in 1.7.10 - use GL11 directly

public interface ColoredRenderable {
    void render(int x, int y, Color c);
}
