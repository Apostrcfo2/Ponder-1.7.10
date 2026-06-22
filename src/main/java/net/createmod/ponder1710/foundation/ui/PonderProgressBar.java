package net.createmod.ponder1710.foundation.ui;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.animation.LerpedFloat.Chaser;
import net.createmod.metanip.data.Couple;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.theme.Color;

import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

// PoseStack/GuiGraphics/AbstractSimiWidget -> GuiButton + GL11 in 1.7.10
public class PonderProgressBar extends GuiButton {

    public static final Couple<Color> BAR_COLORS = Couple.create(
        new Color(0x80_aaaadd, true),
        new Color(0x50_aaaadd, true)
    ).map(Color::setImmutable);

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

    protected boolean isClickable(int mouseX, int mouseY) {
        return enabled && visible
            && ponder.getActiveScene().getKeyframeCount() > 0
            && mouseX >= xPosition && mouseX < xPosition + width + 4
            && mouseY >= yPosition - 3 && mouseY < yPosition + height + 20;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (!isClickable(mouseX, mouseY)) return false;

        PonderScene scene = ponder.getActiveScene();
        int keyframeIndex = getHoveredKeyframeIndex(scene, mouseX);

        if (keyframeIndex == -1)
            ponder.seekToTime(0);
        else if (keyframeIndex == scene.getKeyframeCount())
            ponder.seekToTime(scene.getTotalTime());
        else
            ponder.seekToTime(scene.getKeyframeTime(keyframeIndex));

        return true;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;
        isHovered = isClickable(mouseX, mouseY);
        doRender(mouseX, mouseY, 0);
    }

    public void doRender(int mouseX, int mouseY, float partialTicks) {
        float lerpedProgress = progress.getValue(partialTicks);
        PonderScene activeScene = ponder.getActiveScene();
        Tessellator tess = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Background
        Color bg1 = PonderUI.COLOR_IDLE.getFirst();
        GL11.glColor4f(bg1.getRed()/255f, bg1.getGreen()/255f, bg1.getBlue()/255f, bg1.getAlpha()/255f);
        tess.startDrawingQuads();
        tess.addVertex(xPosition,         yPosition + height, 400);
        tess.addVertex(xPosition + width, yPosition + height, 400);
        tess.addVertex(xPosition + width, yPosition,          400);
        tess.addVertex(xPosition,         yPosition,          400);
        tess.draw();

        // Progress fill
        Color c1 = BAR_COLORS.getFirst();
        GL11.glColor4f(c1.getRed()/255f, c1.getGreen()/255f, c1.getBlue()/255f, c1.getAlpha()/255f);
        int fillW = (int)((width + 4) * lerpedProgress);
        tess.startDrawingQuads();
        tess.addVertex(xPosition - 2,            yPosition + height + 2, 410);
        tess.addVertex(xPosition - 2 + fillW,    yPosition + height + 2, 410);
        tess.addVertex(xPosition - 2 + fillW,    yPosition - 2,          410);
        tess.addVertex(xPosition - 2,            yPosition - 2,          410);
        tess.draw();

        // Keyframe markers
        int totalTime = activeScene.getTotalTime();
        if (totalTime > 0) {
            int hoverIndex = isHovered ? getHoveredKeyframeIndex(activeScene, mouseX) : -2;

            Color hover = PonderUI.COLOR_HOVER.getFirst().setAlpha(0xe0);
            Color idle  = PonderUI.COLOR_HOVER.getFirst().setAlpha(0x70);

            for (int i = 0; i < activeScene.getKeyframeCount(); i++) {
                int kTime = activeScene.getKeyframeTime(i);
                int kX = xPosition - 2 + (int)(((float) kTime) / totalTime * (width + 2));
                boolean selected = i == hoverIndex;
                Color c = selected ? hover : idle;
                int kh = selected ? 8 : 4;
                GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
                tess.startDrawingQuads();
                tess.addVertex(kX,     yPosition + height + kh, 420);
                tess.addVertex(kX + 2, yPosition + height + kh, 420);
                tess.addVertex(kX + 2, yPosition,               420);
                tess.addVertex(kX,     yPosition,               420);
                tess.draw();
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public int getHoveredKeyframeIndex(PonderScene activeScene, double mouseX) {
        int totalTime = activeScene.getTotalTime();
        int clickedAtTime = (int)((mouseX - xPosition) / ((double) width + 4) * totalTime);

        if (activeScene.getKeyframeCount() > 0) {
            int lastKeyframeTime = activeScene.getKeyframeTime(activeScene.getKeyframeCount() - 1);
            int diffToEnd  = totalTime - clickedAtTime;
            int diffToLast = clickedAtTime - lastKeyframeTime;
            if (diffToEnd > 0 && diffToEnd < diffToLast / 2)
                return activeScene.getKeyframeCount();
        }

        int index = -1;
        for (int i = 0; i < activeScene.getKeyframeCount(); i++) {
            if (activeScene.getKeyframeTime(i) > clickedAtTime) break;
            index = i;
        }
        return index;
    }
}
