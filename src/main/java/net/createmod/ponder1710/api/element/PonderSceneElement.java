package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.api.level.PonderLevel;

// In 1.7.10: MultiBufferSource/GuiGraphics/RenderType replaced with GL11 direct rendering
public interface PonderSceneElement extends PonderElement {
    void renderFirst(PonderLevel world, float pt);
    void renderLayer(PonderLevel world, float pt);
    void renderLast(PonderLevel world, float pt);
}
