package net.createmod.ponder1710.api.scene;

import net.createmod.ponder1710.api.ParticleEmitter;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.particles.ParticleOptions; // not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.Vec3;

public interface EffectInstructions {

    void emitParticles(Vec3 location, ParticleEmitter emitter, float amountPerCycle, int cycles);

    // TODO: ParticleOptions not available in 1.7.10 - particle system is different
    // <T extends ParticleOptions> ParticleEmitter simpleParticleEmitter(T data, Vec3 motion);
    // <T extends ParticleOptions> ParticleEmitter particleEmitterWithinBlockSpace(T data, Vec3 motion);

    // BlockPos -> x,y,z in 1.7.10
    void indicateRedstone(int x, int y, int z);

    void indicateSuccess(int x, int y, int z);

    void createRedstoneParticles(int x, int y, int z, int color, int amount);
}
