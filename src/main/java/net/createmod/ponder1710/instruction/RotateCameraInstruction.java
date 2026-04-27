package net.createmod.ponder1710.instruction;

import net.createmod.ponder1710.scene.PonderScene;

public class RotateCameraInstruction extends PonderInstruction {

    private final float targetYaw;
    private final int duration;
    private int elapsed = 0;
    private float startYaw = 0;
    private boolean started = false;

    public RotateCameraInstruction(float yaw, int ticks) {
        this.targetYaw = yaw;
        this.duration = ticks;
    }

    @Override
    public void tick(PonderScene scene) {
        if (!started) {
            startYaw = scene.getCameraYaw();
            started = true;
        }

        elapsed++;
        float progress = Math.min((float) elapsed / duration, 1f);
        float newYaw = startYaw + (targetYaw - startYaw) * progress;
        scene.setCameraYaw(newYaw);

        if (elapsed >= duration) {
            complete = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        elapsed = 0;
        started = false;
    }
}
