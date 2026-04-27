package net.createmod.ponder1710.scene;

import java.util.ArrayList;
import java.util.List;

import net.createmod.ponder1710.instruction.PonderInstruction;

public class PonderScene {

    private final String title;
    private final String schematicPath;
    private final List<PonderInstruction> instructions = new ArrayList<>();
    private int currentInstruction = 0;
    private int timer = 0;

    private float cameraYaw = 225f;
    private float cameraPitch = 30f;

    public float getCameraYaw() { return cameraYaw; }
    public void setCameraYaw(float yaw) { this.cameraYaw = yaw; }
    public float getCameraPitch() { return cameraPitch; }
    public void setCameraPitch(float pitch) { this.cameraPitch = pitch; }

    public PonderScene(String title, String schematicPath) {
        this.title = title;
        this.schematicPath = schematicPath;
    }

    public PonderScene addInstruction(PonderInstruction instruction) {
        instructions.add(instruction);
        return this;
    }

    public void tick() {
        if (currentInstruction >= instructions.size()) return;

        PonderInstruction current = instructions.get(currentInstruction);
        current.tick(this);
        timer++;

        if (current.isComplete()) {
            currentInstruction++;
            timer = 0;
        }
    }

    public void reset() {
        currentInstruction = 0;
        timer = 0;
        for (PonderInstruction instruction : instructions) {
            instruction.reset();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getSchematicPath() {
        return schematicPath;
    }

    public int getTimer() {
        return timer;
    }

    public List<PonderInstruction> getInstructions() {
        return instructions;
    }

    public int getCurrentInstructionIndex() {
        return currentInstruction;
    }

    public boolean isFinished() {
        return currentInstruction >= instructions.size();
    }
}
