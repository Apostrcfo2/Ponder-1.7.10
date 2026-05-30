package net.createmod.ponder1710.enums;


import net.createmod.metanip.theme.Color;
import net.createmod.metanip.theme.Color;
import net.createmod.ponder1710.Ponder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public enum PonderGuiTextures {

    LOGO("logo", 0, 0, 32, 32, 32, 32),

    SPEECH_TOOLTIP_BACKGROUND("widgets", 0, 24, 8, 8),
    SPEECH_TOOLTIP_COLOR("widgets", 8, 24, 8, 8),

    ICON_PONDER_LEFT("widgets", 0, 2),
    ICON_PONDER_CLOSE("widgets", 1, 2),
    ICON_PONDER_RIGHT("widgets", 2, 2),
    ICON_PONDER_IDENTIFY("widgets", 3, 2),
    ICON_PONDER_REPLAY("widgets", 4, 2),
    ICON_PONDER_USER_MODE("widgets", 5, 2),
    ICON_PONDER_SLOW_MODE("widgets", 6, 2),

    PLACEMENT_INDICATOR_SHEET("placement_indicator", 0, 0, 16, 256),

    ICON_LMB("widgets", 7, 2),
    ICON_RMB("widgets", 8, 2),
    ICON_SCROLL("widgets", 9, 2),

    ;

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;
    private final int sheetWidth;
    private final int sheetHeight;

    PonderGuiTextures(String location, int iconColumn, int iconRow) {
        this(location, iconColumn * 16, iconRow * 16, 16, 16);
    }

    PonderGuiTextures(String location, int startX, int startY, int width, int height) {
        this(Ponder.MOD_ID, location, startX, startY, width, height, 256, 256);
    }

    PonderGuiTextures(String location, int startX, int startY, int width, int height, int sheetWidth, int sheetHeight) {
        this(Ponder.MOD_ID, location, startX, startY, width, height, sheetWidth, sheetHeight);
    }

    PonderGuiTextures(String namespace, String location, int startX, int startY, int width, int height, int sheetWidth, int sheetHeight) {
        // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
    }

    public void bind() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    public void render(int x, int y) {
        render(x, y, null);
    }

    public void render(int x, int y, net.createmod.metanip.theme.Color color) {
        bind();
        if (color != null) {
            float r = ((color.getRGB() >> 16) & 0xFF) / 255f;
            float g = ((color.getRGB() >> 8) & 0xFF) / 255f;
            float b = (color.getRGB() & 0xFF) / 255f;
            float a = ((color.getRGB() >> 24) & 0xFF) / 255f;
            GL11.glColor4f(r, g, b, a);
        } else {
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float u1 = startX / (float) sheetWidth;
        float v1 = startY / (float) sheetHeight;
        float u2 = (startX + width) / (float) sheetWidth;
        float v2 = (startY + height) / (float) sheetHeight;

        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x,         y + height, 0, u1, v2);
        tess.addVertexWithUV(x + width, y + height, 0, u2, v2);
        tess.addVertexWithUV(x + width, y,          0, u2, v1);
        tess.addVertexWithUV(x,         y,          0, u1, v1);
        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public ResourceLocation getLocation() { return location; }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
