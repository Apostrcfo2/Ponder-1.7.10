package net.createmod.metanip.gui.element;

import net.createmod.metanip.data.Couple;
import net.createmod.metanip.theme.Color;

import org.lwjgl.opengl.GL11;

public class BoxElement extends AbstractRenderElement {

    public static final Couple<Color> COLOR_VANILLA_BORDER = Couple.create(
        new Color(0x50_5000ff, true), new Color(0x50_28007f, true)
    ).map(Color::setImmutable);
    public static final Color COLOR_VANILLA_BACKGROUND = new Color(0xf0_100010, true).setImmutable();
    public static final Color COLOR_BACKGROUND_FLAT = new Color(0xff_000000, true).setImmutable();
    public static final Color COLOR_BACKGROUND_TRANSPARENT = new Color(0xdd_000000, true).setImmutable();

    protected Color background = COLOR_VANILLA_BACKGROUND;
    protected Color borderTop = COLOR_VANILLA_BORDER.getFirst();
    protected Color borderBot = COLOR_VANILLA_BORDER.getSecond();
    protected int borderOffset = 2;
    protected float alpha = 1f;

    public <T extends BoxElement> T withBackground(Color color) {
        this.background = color; return (T) this;
    }

    public <T extends BoxElement> T withAlpha(float alpha) {
        this.alpha = alpha; return (T) this;
    }

    public <T extends BoxElement> T flatBorder(Color color) {
        this.borderTop = color; this.borderBot = color; return (T) this;
    }

    public <T extends BoxElement> T gradientBorder(Couple<Color> colors) {
        this.borderTop = colors.getFirst(); this.borderBot = colors.getSecond(); return (T) this;
    }

    public <T extends BoxElement> T gradientBorder(Color top, Color bot) {
        this.borderTop = top; this.borderBot = bot; return (T) this;
    }

    public <T extends BoxElement> T withBorderOffset(int offset) {
        this.borderOffset = offset; return (T) this;
    }

    @Override
    public void render() {
        renderBox();
    }

    protected void renderBox() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        int f = borderOffset;
        Color c1 = background.copy().scaleAlpha(alpha);
        Color c2 = borderTop.copy().scaleAlpha(alpha);
        Color c3 = borderBot.copy().scaleAlpha(alpha);

        GL11.glBegin(GL11.GL_QUADS);

        // background
        setColor(c1);
        GL11.glVertex3f(x - f - 1, y - f - 1, z);
        GL11.glVertex3f(x - f - 1, y + f + 1 + height, z);
        GL11.glVertex3f(x + f + 1 + width, y + f + 1 + height, z);
        GL11.glVertex3f(x + f + 1 + width, y - f - 1, z);

        // top border
        setColor(c2);
        GL11.glVertex3f(x - f - 1, y - f - 1, z);
        GL11.glVertex3f(x - f - 1, y - f, z);
        GL11.glVertex3f(x + f + 1 + width, y - f, z);
        GL11.glVertex3f(x + f + 1 + width, y - f - 1, z);

        // bottom border
        setColor(c3);
        GL11.glVertex3f(x - f - 1, y + f + height, z);
        GL11.glVertex3f(x - f - 1, y + f + 1 + height, z);
        GL11.glVertex3f(x + f + 1 + width, y + f + 1 + height, z);
        GL11.glVertex3f(x + f + 1 + width, y + f + height, z);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void setColor(Color c) {
        GL11.glColor4f(c.getRedAsFloat(), c.getGreenAsFloat(), c.getBlueAsFloat(), c.getAlphaAsFloat());
    }
}
