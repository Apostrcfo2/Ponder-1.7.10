package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

// AABB -> AxisAlignedBB in 1.7.10
// Outliner available from metanip
public class HighlightValueBoxInstruction extends TickingInstruction {

    private final Vec3 vec;
    private final Vec3 expands;

    public HighlightValueBoxInstruction(Vec3 vec, Vec3 expands, int duration) {
        super(false, duration);
        this.vec = vec;
        this.expands = expands;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        AxisAlignedBB point = AxisAlignedBB.getBoundingBox(
            vec.xCoord, vec.yCoord, vec.zCoord,
            vec.xCoord, vec.yCoord, vec.zCoord);
        AxisAlignedBB expanded = point.expand(expands.xCoord, expands.yCoord, expands.zCoord);
        scene.getOutliner()
            .chaseAABB(vec, remainingTicks + 1 >= totalTicks ? point : expanded)
            .lineWidth(1 / 15f)
            .colored(PonderPalette.WHITE.getColor());
    }
}
