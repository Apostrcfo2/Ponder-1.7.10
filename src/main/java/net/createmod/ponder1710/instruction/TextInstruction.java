package net.createmod.ponder1710.instruction;

import net.createmod.ponder1710.scene.PonderScene;

public class TextInstruction extends PonderInstruction {

    private final String text;
    private final int duration;
    private int elapsed = 0;

    public TextInstruction(String text, int ticks) {
        this.text = text;
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

    public String getText() {
        return text;
    }

    public float getProgress() {
        return (float) elapsed / duration;
    }
}
