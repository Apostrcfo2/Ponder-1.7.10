package net.createmod.ponder1710.instruction;

import net.createmod.ponder1710.scene.PonderScene;

public abstract class PonderInstruction {

    protected boolean complete = false;

    public abstract void tick(PonderScene scene);

    public boolean isComplete() {
        return complete;
    }

    public void reset() {
        complete = false;
    }
}
