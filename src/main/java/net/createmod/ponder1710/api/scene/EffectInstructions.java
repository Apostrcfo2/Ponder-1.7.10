package net.createmod.ponder1710.api.scene;

import net.createmod.ponder1710.api.ParticleEmitter;
import net.createmod.ponder1710.api.ParticleFactory;
import net.minecraft.util.Vec3;

// ParticleOptions -> ParticleFactory in 1.7.10
// BlockPos -> x,y,z ints in 1.7.10
public interface EffectInstructions {

    void emitParticles(Vec3 location, ParticleEmitter emitter, float amountPerCycle, int cycles);

    // ParticleOptions replaced by ParticleFactory (functional interface for EntityFX creation)
    ParticleEmitter simpleParticleEmitter(ParticleFactory factory, Vec3 motion);

    ParticleEmitter particleEmitterWithinBlockSpace(ParticleFactory factory, Vec3 motion);

    void indicateRedstone(int x, int y, int z);

    void indicateSuccess(int x, int y, int z);

    void createRedstoneParticles(int x, int y, int z, int color, int amount);
}
