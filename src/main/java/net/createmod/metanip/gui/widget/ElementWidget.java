package net.createmod.metanip.gui.widget;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.gui.element.AbstractRenderElement;
import net.createmod.metanip.gui.element.RenderElement;
import net.createmod.metanip.gui.element.ScreenElement;

import org.lwjgl.opengl.GL11;

// GuiGraphics/PoseStack not available in 1.7.10 - use GL11

public class ElementWidget extends AbstractSimiWidget {

    protected RenderElement element = AbstractRenderElement.EMPTY;
    protected boolean usesFade = false;
    protected int fadeModX, fadeModY;
    protected LerpedFloat fade = LerpedFloat.linear().startWithValue(1);
    protected boolean rescaleElement = false;
    protected float rescaleSizeX, rescaleSizeY;
    protected float paddingX = 0, paddingY = 0;

    public ElementWidget(int x, int y) { super(x, y, 16, 16); }
    public ElementWidget(int x, int y, int width, int height) { super(x, y, width, height); }

    public <T extends ElementWidget> T showingElement(RenderElement element) {
        this.element = element; return (T) this;
    }

    public <T extends ElementWidget> T showing(ScreenElement renderable) {
        return showingElement(RenderElement.of(renderable));
    }

    public <T extends ElementWidget> T modifyElement(Consumer<RenderElement> consumer) {
        consumer.accept(element); return (T) this;
    }

    public <T extends ElementWidget> T mapElement(UnaryOperator<RenderElement> function) {
        element = function.apply(element); return (T) this;
    }

    public <T extends ElementWidget> T withPadding(float paddingX, float paddingY) {
        this.paddingX = paddingX; this.paddingY = paddingY; return (T) this;
    }

    public <T extends ElementWidget> T enableFade(int fadeModX, int fadeModY) {
        fade.startWithValue(0); usesFade = true;
        this.fadeModX = fadeModX; this.fadeModY = fadeModY;
        return (T) this;
    }

    public <T extends ElementWidget> T disableFade() {
        fade.startWithValue(1); usesFade = false; return (T) this;
    }

    public LerpedFloat fade() { return fade; }

    public <T extends ElementWidget> T fade(float target) {
        fade.chase(target, 0.1, LerpedFloat.Chaser.EXP); return (T) this;
    }

    @Override
    public void tick() {
        super.tick();
        fade.tickChaser();
    }

    @Override
    protected void doRender(int mouseX, int mouseY, float partialTicks) {
        float fadeValue = fade.getValue(partialTicks);
        element.withAlpha(fadeValue);

        GL11.glPushMatrix();
        GL11.glTranslatef(
            xPosition + paddingX + (1 - fadeValue) * fadeModX,
            yPosition + paddingY + (1 - fadeValue) * fadeModY,
            z
        );

        float innerWidth = width - 2 * paddingX;
        float innerHeight = height - 2 * paddingY;

        if (rescaleElement) {
            float xScale = innerWidth / rescaleSizeX;
            float yScale = innerHeight / rescaleSizeY;
            GL11.glScalef(xScale, yScale, 1);
            innerWidth /= xScale;
            innerHeight /= yScale;
        }

        element.withBounds((int) innerWidth, (int) innerHeight).render();
        GL11.glPopMatrix();
    }

    public RenderElement getRenderElement() { return element; }
}
