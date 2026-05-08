package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.ParticleEmitter;
import net.createmod.ponder1710.foundation.PonderScene;

// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.Vec3;

public class EmitParticlesInstruction extends TickingInstruction {

    private final Vec3 anchor;
    private final ParticleEmitter emitter;
    private final float runsPerTick;

    public EmitParticlesInstruction(Vec3 anchor, ParticleEmitter emitter, float runsPerTick, int ticks) {
        super(false, ticks);
        this.anchor = anchor;
        this.emitter = emitter;
        this.runsPerTick = runsPerTick;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        int runs = (int) runsPerTick;
        if (Ponder.RANDOM.nextFloat() < (runsPerTick - runs))
            runs++;
        for (int i = 0; i < runs; i++)
            emitter.create(scene.getWorld(), anchor.xCoord, anchor.yCoord, anchor.zCoord);
    }
}
