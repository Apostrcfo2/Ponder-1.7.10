package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderScene.SceneTransform;

// TODO: LerpedFloat.Chaser from catnip not available in 1.7.10
// Replaced with direct value setting until LerpedFloat is ported
public class RotateSceneInstruction extends PonderInstruction {

    private final float xRot;
    private final float yRot;
    private final boolean relative;

    public RotateSceneInstruction(float xRot, float yRot, boolean relative) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.relative = relative;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void tick(PonderScene scene) {
        SceneTransform transform = scene.getTransform();
        float targetX = relative ? transform.xRotation + xRot : xRot;
        float targetY = relative ? transform.yRotation + yRot : yRot;
        // TODO: Use smooth chasing once LerpedFloat is ported from catnip
        // transform.xRotation.chase(targetX, .1f, Chaser.EXP);
        // transform.yRotation.chase(targetY, .1f, Chaser.EXP);
        transform.xRotation = targetX;
        transform.yRotation = targetY;
    }
}
