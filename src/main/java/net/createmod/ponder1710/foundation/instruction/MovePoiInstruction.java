package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.util.Vec3;

public class MovePoiInstruction extends PonderInstruction {

    private final Vec3 poi;

    public MovePoiInstruction(Vec3 poi) {
        this.poi = poi;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void tick(PonderScene scene) {
        scene.setPointOfInterest(poi);
    }
}
