package net.createmod.ponder1710.foundation.ui;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.animation.LerpedFloat.Chaser;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.theme.Color;
// import net.minecraft.ChatFormatting; // EnumChatFormatting in 1.7.10
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.sounds.SoundManager; // different in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10

import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class PonderProgressBar extends GuiButton {

    LerpedFloat progress = LerpedFloat.linear().startWithValue(0);

    PonderUI ponder;

    public PonderProgressBar(PonderUI ponder, int x, int y, int width, int height) {
        super(0, x, y, width, height, "");
        this.ponder = ponder;
    }

    public void tick() {
        progress.chase(ponder.getActiveScene().getSceneProgress(), 0.5f, Chaser.EXP);
        progress.tickChaser();
    }

    public void doRender(int mouseX, int mouseY, float partialTicks) {
        float lerpedProgress = progress.getValue(partialTicks);
        PonderScene activeScene = ponder.getActiveScene();

        // Background
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0, 0, 0, 0.4f);
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertex(xPosition, yPosition + height, 0);
        tess.addVertex(xPosition + width, yPosition + height, 0);
        tess.addVertex(xPosition + width, yPosition, 0);
        tess.addVertex(xPosition, yPosition, 0);
        tess.draw();

        // Progress fill
        float r = 0.8f, g = 0.9f, b = 1.0f;
        GL11.glColor4f(r, g, b, 0.8f);
        int fillW = (int)(width * lerpedProgress);
        tess.startDrawingQuads();
        tess.addVertex(xPosition, yPosition + height, 0);
        tess.addVertex(xPosition + fillW, yPosition + height, 0);
        tess.addVertex(xPosition + fillW, yPosition, 0);
        tess.addVertex(xPosition, yPosition, 0);
        tess.draw();

        // Keyframe markers
        GL11.glColor4f(1f, 1f, 1f, 0.6f);
        int totalTime = activeScene.getTotalTime();
        for (int k = 0; k < activeScene.getKeyframeCount(); k++) {
            int kTime = activeScene.getKeyframeTime(k);
            int kX = xPosition + (int)(width * kTime / (float)totalTime);
            tess.startDrawingQuads();
            tess.addVertex(kX - 1, yPosition + height + 2, 0);
            tess.addVertex(kX + 1, yPosition + height + 2, 0);
            tess.addVertex(kX + 1, yPosition - 2, 0);
            tess.addVertex(kX - 1, yPosition - 2, 0);
            tess.draw();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public int getHoveredKeyframeIndex(PonderScene activeScene, double mouseX) {
        int totalTime = activeScene.getTotalTime();
        int clickedAtTime = (int) ((mouseX - xPosition) / ((double) width + 4) * totalTime);

        if (activeScene.getKeyframeCount() > 0) {
            int lastKeyframeTime = activeScene.getKeyframeTime(activeScene.getKeyframeCount() - 1);
            int diffToEnd = totalTime - clickedAtTime;
            int diffToLast = clickedAtTime - lastKeyframeTime;
            if (diffToEnd > 0 && diffToEnd < diffToLast / 2)
                return activeScene.getKeyframeCount();
        }

        int index = -1;
        for (int i = 0; i < activeScene.getKeyframeCount(); i++) {
            if (activeScene.getKeyframeTime(i) > clickedAtTime)
                break;
            index = i;
        }
        return index;
    }
}
