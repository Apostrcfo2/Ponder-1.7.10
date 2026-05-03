package net.createmod.ponder1710.foundation.element;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import net.createmod.catnip.animation.LerpedFloat; // TODO: catnip not available - replaced with float
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.renderer.LightTexture; // not available in 1.7.10
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10
// import net.minecraft.client.renderer.RenderType; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public abstract class AnimatedSceneElementBase extends PonderElementBase implements AnimatedSceneElement {

    protected Vec3 fadeVec;
    // TODO: LerpedFloat from catnip not available - replaced with plain float
    protected float fadeValue = 0;
    protected float prevFadeValue = 0;

    @Override
    public void forceApplyFade(float fade) {
        this.fadeValue = fade;
        this.prevFadeValue = fade;
    }

    @Override
    public void setFade(float fade) {
        this.prevFadeValue = this.fadeValue;
        this.fadeValue = fade;
    }

    @Override
    public void setFadeVec(Vec3 fadeVec) {
        this.fadeVec = fadeVec;
    }

    @Override
    public final void renderFirst(PonderLevel world, float pt) {
        GL11.glPushMatrix();
        float currentFade = applyFade(pt);
        renderFirst(world, currentFade, pt);
        GL11.glPopMatrix();
    }

    @Override
    public final void renderLayer(PonderLevel world, float pt) {
        GL11.glPushMatrix();
        float currentFade = applyFade(pt);
        renderLayer(world, currentFade, pt);
        GL11.glPopMatrix();
    }

    @Override
    public final void renderLast(PonderLevel world, float pt) {
        GL11.glPushMatrix();
        float currentFade = applyFade(pt);
        renderLast(world, currentFade, pt);
        GL11.glPopMatrix();
    }

    protected float applyFade(float pt) {
        float currentFade = MathHelper.lerp(pt, prevFadeValue, fadeValue);
        if (fadeVec != null) {
            double sx = fadeVec.xCoord * (-1 + currentFade);
            double sy = fadeVec.yCoord * (-1 + currentFade);
            double sz = fadeVec.zCoord * (-1 + currentFade);
            GL11.glTranslated(sx, sy, sz);
        }
        return currentFade;
    }

    protected void renderLayer(PonderLevel world, float fade, float pt) {}

    protected void renderFirst(PonderLevel world, float fade, float pt) {}

    protected void renderLast(PonderLevel world, float fade, float pt) {}

    protected int lightCoordsFromFade(float fade) {
        // TODO: LightTexture not available in 1.7.10 - simplified
        if (fade == 1)
            return 0xF000F0;
        int light = (int) MathHelper.lerp(fade, 5, 0xF);
        return light << 20 | light << 4;
    }
}
