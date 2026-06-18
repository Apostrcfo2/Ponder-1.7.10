package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.util.AxisAlignedBB;

// AABB -> AxisAlignedBB in 1.7.10
// Outliner available from metanip
public class ChaseAABBInstruction extends TickingInstruction {

    private final AxisAlignedBB bb;
    private final Object slot;
    private final PonderPalette color;

    public ChaseAABBInstruction(PonderPalette color, Object slot, AxisAlignedBB bb, int ticks) {
        super(false, ticks);
        this.color = color;
        this.slot = slot;
        this.bb = bb;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        scene.getOutliner()
            .chaseAABB(slot, bb)
            .lineWidth(1 / 16f)
            .colored(color.getColor());
    }
}
