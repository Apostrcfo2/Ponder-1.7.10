package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public interface PonderSceneElement extends PonderElement {

	void renderFirst(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float pt);

	void renderLayer(PonderLevel world, MultiBufferSource buffer, RenderType type, GuiGraphics graphics, float pt);

	void renderLast(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float pt);

}
