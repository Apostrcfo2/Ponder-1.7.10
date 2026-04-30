package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.api.level.PonderLevel;

// import net.minecraft.client.gui.GuiGraphics; // TODO: not available in 1.7.10 - use GL11
// import net.minecraft.client.renderer.MultiBufferSource; // TODO: not available in 1.7.10
// import net.minecraft.client.renderer.RenderType; // TODO: not available in 1.7.10

public interface PonderSceneElement extends PonderElement {

    // TODO: MultiBufferSource and GuiGraphics replaced with GL11 in 1.7.10
    void renderFirst(PonderLevel world, float pt);

    // TODO: RenderType not available in 1.7.10 - rendering layers handled differently
    void renderLayer(PonderLevel world, float pt);

    void renderLast(PonderLevel world, float pt);
}
