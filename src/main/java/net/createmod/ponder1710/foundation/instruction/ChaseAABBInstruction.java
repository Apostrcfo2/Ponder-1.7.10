package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.foundation.PonderScene;

// import net.minecraft.world.phys.AABB; // AxisAlignedBB in 1.7.10
import net.minecraft.util.AxisAlignedBB;

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
        // TODO: scene.getOutliner() - Outliner from catnip not available in 1.7.10
        // scene.getOutliner().chaseAABB(slot, bb).lineWidth(1 / 16f).colored(color.getColor());
    }
}
