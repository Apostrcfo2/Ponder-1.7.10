package net.createmod.ponder1710.foundation.element;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.level.PonderLevel;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

// MultiBufferSource/GuiGraphics/PoseStack/RenderType -> GL11 direct in 1.7.10
// LightTexture -> lightCoordsFromFade helper
public abstract class AnimatedSceneElementBase extends PonderElementBase implements AnimatedSceneElement {

    protected Vec3 fadeVec;
    protected LerpedFloat fade;

    public AnimatedSceneElementBase() {
        fade = LerpedFloat.linear().startWithValue(0);
    }

    @Override
    public void forceApplyFade(float fade) {
        this.fade.startWithValue(fade);
    }

    @Override
    public void setFade(float fade) {
        this.fade.setValue(fade);
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
        float currentFade = fade.getValue(pt);
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
    protected void renderLast(PonderLevel world, float fade, float pt)  {}

    // Full bright = 0xF000F0, else lerp between dim and full
    protected int lightCoordsFromFade(float fade) {
        if (fade == 1) return 0xF000F0;
        int light = (int) MathHelper.lerp(fade, 5, 0xF);
        return light << 20 | light << 4;
    }
}
