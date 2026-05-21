package net.createmod.metanip.gui.element;

import net.createmod.metanip.theme.Color;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

// MutableComponent/Component not available in 1.7.10 - use plain String
// Font -> FontRenderer in 1.7.10
// GuiGraphics/PoseStack -> GL11

public class TextStencilElement extends DelegatedStencilElement {

    protected FontRenderer font;
    protected String text = "";
    protected boolean centerVertically = false;
    protected boolean centerHorizontally = false;

    public TextStencilElement(FontRenderer font) {
        super();
        this.font = font;
        height = 10;
    }

    public TextStencilElement(FontRenderer font, String text) {
        this(font);
        this.text = text;
    }

    public TextStencilElement withText(String text) {
        this.text = text;
        return this;
    }

    public TextStencilElement centered(boolean vertical, boolean horizontal) {
        this.centerVertically = vertical;
        this.centerHorizontally = horizontal;
        return this;
    }

    @Override
    public void renderStencil() {
        float px = 0, py = 0;
        if (centerHorizontally) px = width / 2f - font.getStringWidth(text) / 2f;
        if (centerVertically)   py = height / 2f - (font.FONT_HEIGHT - 1) / 2f;

        GL11.glPushMatrix();
        GL11.glTranslatef(px, py, 0);
        font.drawString(text, 0, 0, Color.BLACK.getRGB());
        GL11.glPopMatrix();
    }

    @Override
    public void renderElement() {
        float px = 0, py = 0;
        if (centerHorizontally) px = width / 2f - font.getStringWidth(text) / 2f;
        if (centerVertically)   py = height / 2f - (font.FONT_HEIGHT - 1) / 2f;

        GL11.glPushMatrix();
        GL11.glTranslatef(px, py, 0);
        element.render(font.getStringWidth(text), font.FONT_HEIGHT + 2, alpha);
        GL11.glPopMatrix();
    }

    public String getText() { return text; }
}
