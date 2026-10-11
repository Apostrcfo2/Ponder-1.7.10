package net.createmod.metanip.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import net.createmod.metanip.data.Couple;
import net.createmod.metanip.theme.Color;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

// Removed: RenderSystem, PoseStack, GuiGraphics, BufferBuilder, Tesselator, RenderTarget
// Replaced with GL11 direct calls

public class UIRenderHelper {

    public static final Couple<Color> COLOR_TEXT = Couple.create(
        new Color(0xff_eeeeee), new Color(0xff_a3a3a3)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_TEXT_DARKER = Couple.create(
        new Color(0xff_a3a3a3), new Color(0xff_808080)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_TEXT_ACCENT = Couple.create(
        new Color(0xff_ddeeff), new Color(0xff_a0b0c0)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_TEXT_STRONG_ACCENT = Couple.create(
        new Color(0xff_8ab6d6), new Color(0xff_6e92ab)
    ).map(Color::setImmutable);
    public static final Color COLOR_STREAK = new Color(0x101010, false).setImmutable();

    public static void init() {
        // TODO: framebuffer setup not needed for basic rendering in 1.7.10
    }

    public static void streak(float angle, int x, int y, int breadth, int length) {
        streak(angle, x, y, breadth, length, COLOR_STREAK);
    }

    public static void streak(float angle, int x, int y, int breadth, int length, Color c) {
        Color color = c.copy().setImmutable();
        Color c1 = color.scaleAlpha(0.625f);
        Color c2 = color.scaleAlpha(0.5f);
        Color c3 = color.scaleAlpha(0.0625f);
        Color c4 = color.scaleAlpha(0f);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(angle - 90, 0, 0, 1);
        streakDraw(breadth / 2, length, c1, c2, c3, c4);
        GL11.glPopMatrix();
    }

    private static void streakDraw(int width, int height, Color c1, Color c2, Color c3, Color c4) {
        if (NavigatableSimiScreen.isCurrentlyRenderingPreviousScreen()) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);

        double split1 = .5, split2 = .75;
        fillGradientGL(-width, 0, width, (int)(split1 * height), c1, c2);
        fillGradientGL(-width, (int)(split1 * height), width, (int)(split2 * height), c2, c3);
        fillGradientGL(-width, (int)(split2 * height), width, height, c3, c4);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static void fillGradientGL(int x1, int y1, int x2, int y2, Color top, Color bot) {
        setColor(top);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y1);
        setColor(bot);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x1, y2);
    }

    public static void angledGradient(float angle, int x, int y, float breadth, float length, Couple<Color> c) {
        angledGradient(angle, x, y, 0, breadth, length, c.getFirst(), c.getSecond());
    }

    public static void angledGradient(float angle, int x, int y, int z, float breadth, float length, Couple<Color> c) {
        angledGradient(angle, x, y, z, breadth, length, c.getFirst(), c.getSecond());
    }

    public static void angledGradient(float angle, int x, int y, int z, float breadth, float length, Color startColor, Color endColor) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(angle - 90, 0, 0, 1);
        drawGradientRect(z, -breadth/2, 0, breadth/2, length, startColor, endColor);
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(int zLevel, float left, float top, float right, float bottom, Color startColor, Color endColor) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);

        setColor(startColor);
        GL11.glVertex3f(right, top, zLevel);
        GL11.glVertex3f(left, top, zLevel);
        setColor(endColor);
        GL11.glVertex3f(left, bottom, zLevel);
        GL11.glVertex3f(right, bottom, zLevel);

        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void breadcrumbArrow(int x, int y, int z, int width, int height, int indent, Couple<Color> colors) {
        breadcrumbArrow(x, y, z, width, height, indent, colors.getFirst(), colors.getSecond());
    }

    public static void breadcrumbArrow(int x, int y, int z, int width, int height, int indent, Color startColor, Color endColor) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x - indent, y, z);
        breadcrumbArrowDraw(width, height, indent, startColor, endColor);
        GL11.glPopMatrix();
    }

    private static void breadcrumbArrowDraw(int width, int height, int indent, Color c1, Color c2) {
        float x0 = 0, x1 = indent, x2 = width, x3 = indent + width;
        float y0 = 0, y1 = height / 2f, y2 = height;

        Color fc1 = Color.mixColors(c1, c2, 0);
        Color fc2 = Color.mixColors(c1, c2, (float) indent / (width + 2f * indent));
        Color fc3 = Color.mixColors(c1, c2, (float)(indent + width) / (width + 2f * indent));
        Color fc4 = Color.mixColors(c1, c2, 1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_TRIANGLES);

        tri(fc1, x0, y1, fc2, x1, y0, fc2, x1, y1);
        tri(fc1, x0, y1, fc2, x1, y1, fc2, x1, y2);
        tri(fc2, x1, y2, fc2, x1, y0, fc3, x2, y0);
        tri(fc2, x1, y2, fc3, x2, y0, fc3, x2, y2);
        tri(fc3, x2, y1, fc3, x2, y0, fc4, x3, y0);
        tri(fc3, x2, y2, fc3, x2, y1, fc4, x3, y2);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static void tri(Color ca, float ax, float ay, Color cb, float bx, float by, Color cc, float cx, float cy) {
        setColor(ca); GL11.glVertex2f(ax, ay);
        setColor(cb); GL11.glVertex2f(bx, by);
        setColor(cc); GL11.glVertex2f(cx, cy);
    }

    public static void drawColoredTexture(Color c, int x, int y, int texLeft, int texTop, int width, int height) {
        drawColoredTexture(c, x, y, 0, texLeft, texTop, width, height, 256, 256);
    }

    public static void drawColoredTexture(Color c, int x, int y, int z, float texLeft, float texTop, int width, int height, int sheetW, int sheetH) {
        float u1 = texLeft / sheetW, u2 = (texLeft + width) / sheetW;
        float v1 = texTop / sheetH, v2 = (texTop + height) / sheetH;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(c.getRedAsFloat(), c.getGreenAsFloat(), c.getBlueAsFloat(), c.getAlphaAsFloat());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v2); GL11.glVertex3f(x, y + height, z);
        GL11.glTexCoord2f(u2, v2); GL11.glVertex3f(x + width, y + height, z);
        GL11.glTexCoord2f(u2, v1); GL11.glVertex3f(x + width, y, z);
        GL11.glTexCoord2f(u1, v1); GL11.glVertex3f(x, y, z);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void flipForGuiRender() {
        GL11.glScalef(1, -1, 1);
    }

    private static void setColor(Color c) {
        GL11.glColor4f(c.getRedAsFloat(), c.getGreenAsFloat(), c.getBlueAsFloat(), c.getAlphaAsFloat());
    }
}
