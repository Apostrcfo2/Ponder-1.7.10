package net.createmod.metanip.render;

import net.createmod.metanip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;

public interface ColoredRenderable {

	void render(GuiGraphics graphics, int x, int y, Color c);

}
