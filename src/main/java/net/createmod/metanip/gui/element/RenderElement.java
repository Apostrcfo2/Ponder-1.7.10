package net.createmod.metanip.gui.element;

// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10

public interface RenderElement extends FadableScreenElement {

    static RenderElement of(ScreenElement renderable) {
        return new AbstractRenderElement() {
            @Override
            public void render() {
                renderable.render((int) x, (int) y);
            }
        };
    }

    <T extends RenderElement> T at(float x, float y);
    <T extends RenderElement> T at(float x, float y, float z);
    <T extends RenderElement> T withBounds(int width, int height);
    <T extends RenderElement> T withAlpha(float alpha);

    int getWidth();
    int getHeight();
    float getX();
    float getY();
    float getZ();

    void render();

    @Override
    default void render(int x, int y, float alpha) {
        this.at(x, y).withAlpha(alpha).render();
    }
}
