package net.createmod.metanip.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.ponder1710.mixin.client.accessor.ScreenAccessor; // not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.gui.components.*; // not available in 1.7.10
// import net.minecraft.network.chat.*; // not available in 1.7.10

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public abstract class AbstractSimiScreen extends GuiScreen {

    protected static final Color BACKGROUND_COLOR = new Color(0x50_101010, true);

    protected int windowWidth, windowHeight;
    protected int windowXOffset, windowYOffset;
    protected int guiLeft, guiTop;

    protected void setWindowSize(int width, int height) {
        windowWidth = width;
        windowHeight = height;
    }

    protected void setWindowOffset(int xOffset, int yOffset) {
        windowXOffset = xOffset;
        windowYOffset = yOffset;
    }

    @Override
    public void initGui() {
        guiLeft = (width - windowWidth) / 2 + windowXOffset;
        guiTop = (height - windowHeight) / 2 + windowYOffset;
    }

    @Override
    public void updateScreen() {
        // tick widgets
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float pt = NavigatableSimiScreen.currentlyRenderingPreviousScreen ? 0 : AnimationTickHolder.getPartialTicksUI();

        GL11.glPushMatrix();
        renderWindowBackground(mouseX, mouseY, pt);
        renderWindow(mouseX, mouseY, pt);
        super.drawScreen(mouseX, mouseY, pt);
        renderWindowForeground(mouseX, mouseY, pt);
        GL11.glPopMatrix();
    }

    protected void renderWindowBackground(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
    }

    protected abstract void renderWindow(int mouseX, int mouseY, float partialTicks);

    protected void renderWindowForeground(int mouseX, int mouseY, float partialTicks) {}

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
