package net.createmod.metanip.render;

// In 1.7.10 there are no RenderType or VertexConsumer systems.
// This interface is simplified to just support draw() for Ponder's needs.
// All rendering is done directly via GL11.

public interface SuperRenderTypeBuffer {

    void draw();

    // Stub: in 1.7.10 all rendering goes through GL11 directly
    // These methods exist for API compatibility with Ponder calls
    default void drawEarly() {}
    default void drawLate() {}
}
