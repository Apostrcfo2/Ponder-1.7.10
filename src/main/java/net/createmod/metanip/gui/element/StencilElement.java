package net.createmod.metanip.gui.element;

// GuiGraphics not available in 1.7.10 - use GL11 directly
public interface StencilElement {
    void renderStencil();
    void renderElement();
}
