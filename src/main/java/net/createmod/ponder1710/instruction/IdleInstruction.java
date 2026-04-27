package net.createmod.ponder1710.instruction;

import net.createmod.ponder1710.scene.PonderScene;

public class IdleInstruction extends PonderInstruction {

    private final int duration;
    private int elapsed = 0;

    public IdleInstruction(int ticks) {
        this.duration = ticks;
    }

    @Override
    public void tick(PonderScene scene) {
        elapsed++;
        if (elapsed >= duration) {
            complete = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        elapsed = 0;
    }
}
