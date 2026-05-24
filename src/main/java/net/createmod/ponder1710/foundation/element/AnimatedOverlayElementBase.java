package net.createmod.ponder1710.foundation.element;

// import net.createmod.metanip.animation.LerpedFloat; // TODO: catnip not available - replaced with float

import net.createmod.ponder1710.api.element.AnimatedOverlayElement;
import net.minecraft.util.MathHelper;

public abstract class AnimatedOverlayElementBase extends PonderElementBase implements AnimatedOverlayElement {

    // TODO: LerpedFloat from catnip not available - replaced with plain float
    protected float fadeValue = 0;
    protected float prevFadeValue = 0;

    @Override
    public void setFade(float fade) {
        this.prevFadeValue = this.fadeValue;
        this.fadeValue = fade;
    }

    @Override
    public float getFade(float partialTicks) {
        return MathHelper.lerp(partialTicks, prevFadeValue, fadeValue);
    }
}
