package net.createmod.metanip.gui.element;

// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10 - use GL11

public abstract class AbstractRenderElement implements RenderElement {

    public static RenderElement EMPTY = new AbstractRenderElement() {
        @Override
        public void render() {}
    };

    protected int width = 16, height = 16;
    protected float x = 0, y = 0, z = 0;
    protected float alpha = 1f;

    @Override
    public <T extends RenderElement> T at(float x, float y) {
        this.x = x; this.y = y;
        return (T) this;
    }

    @Override
    public <T extends RenderElement> T at(float x, float y, float z) {
        this.x = x; this.y = y; this.z = z;
        return (T) this;
    }

    @Override
    public <T extends RenderElement> T withBounds(int width, int height) {
        this.width = width; this.height = height;
        return (T) this;
    }

    @Override
    public <T extends RenderElement> T withAlpha(float alpha) {
        this.alpha = alpha;
        return (T) this;
    }

    @Override public int getWidth()  { return width; }
    @Override public int getHeight() { return height; }
    @Override public float getX()    { return x; }
    @Override public float getY()    { return y; }
    @Override public float getZ()    { return z; }
}
