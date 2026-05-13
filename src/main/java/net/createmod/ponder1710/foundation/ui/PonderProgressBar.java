package net.createmod.ponder1710.foundation.ui;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.catnip.animation.LerpedFloat; // TODO: catnip not available
// import net.createmod.catnip.data.Couple; // TODO: catnip not available
// import net.createmod.catnip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.catnip.gui.element.BoxElement; // TODO: catnip not available
// import net.createmod.catnip.gui.widget.AbstractSimiWidget; // TODO: catnip not available
// import net.createmod.catnip.theme.Color; // TODO: catnip not available
// import net.minecraft.ChatFormatting; // EnumChatFormatting in 1.7.10
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.sounds.SoundManager; // different in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10

import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class PonderProgressBar extends GuiButton {

    // TODO: LerpedFloat from catnip not available - replaced with float
    float progress = 0;
    float prevProgress = 0;

    PonderUI ponder;

    public PonderProgressBar(PonderUI ponder, int x, int y, int width, int height) {
        super(0, x, y, width, height, "");
        this.ponder = ponder;
    }

    public void tick() {
        prevProgress = progress;
        // TODO: smooth chase with LerpedFloat not available
        float target = ponder.getActiveScene().getSceneProgress();
        progress += (target - progress) * 0.5f;
    }

    // TODO: doRender - BoxElement/UIRenderHelper/GuiGraphics not available in 1.7.10
    // Full reimplementation needed using GL11
    public void doRender(int mouseX, int mouseY, float partialTicks) {
        float lerpedProgress = prevProgress + (progress - prevProgress) * partialTicks;
        // TODO: render progress bar using GL11
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
