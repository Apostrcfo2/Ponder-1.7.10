package net.createmod.ponder1710.enums;

// import net.createmod.catnip.gui.TextureSheetSegment; // TODO: catnip not available
// import net.createmod.catnip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.catnip.gui.element.DelegatedStencilElement; // TODO: catnip not available
// import net.createmod.catnip.gui.element.ScreenElement; // TODO: catnip not available
// import net.createmod.catnip.render.ColoredRenderable; // TODO: catnip not available
// import net.createmod.catnip.theme.Color; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10

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

    // TODO: render(GuiGraphics, int, int) - GuiGraphics not available in 1.7.10
    // Use GL11 directly instead
    public void render(int x, int y) {
        bind();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        // TODO: drawTexturedModalRect equivalent
    }

    public ResourceLocation getLocation() { return location; }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
