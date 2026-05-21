package net.createmod.metanip.gui.element;

import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.theme.Color;

// GuiGraphics not available in 1.7.10 - use GL11

public class DelegatedStencilElement extends AbstractRenderElement implements StencilElement {

    protected static final FadableScreenElement EMPTY_RENDERER = (x, y, alpha) -> {};
    protected static final FadableScreenElement DEFAULT_ELEMENT = (w, h, alpha) ->
        UIRenderHelper.angledGradient(0, -3, 5, 0, h + 4, w + 6,
            new Color(0xff_10dd10).scaleAlpha(alpha),
            new Color(0xff_1010dd).scaleAlpha(alpha));

    protected FadableScreenElement stencil;
    protected FadableScreenElement element;

    public DelegatedStencilElement() {
        stencil = EMPTY_RENDERER;
        element = DEFAULT_ELEMENT;
    }

    public DelegatedStencilElement(FadableScreenElement stencil, FadableScreenElement element) {
        this.stencil = stencil;
        this.element = element;
    }

    public <T extends DelegatedStencilElement> T withStencilRenderer(FadableScreenElement renderer) {
        stencil = renderer; return (T) this;
    }

    public <T extends DelegatedStencilElement> T withElementRenderer(FadableScreenElement renderer) {
        element = renderer; return (T) this;
    }

    @Override
    public void renderStencil() {
        stencil.render(width, height, 1);
    }

    @Override
    public void renderElement() {
        element.render(width, height, alpha);
    }

    @Override
    public void render() {
        renderStencil();
        renderElement();
    }
}
