package net.createmod.ponder1710.api;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.minecraft.client.particle.EntityFX;

// Replaces ParticleOptions in 1.7.10 - functional interface for creating EntityFX particles
@FunctionalInterface
public interface ParticleFactory {
    EntityFX create(PonderLevel world, double x, double y, double z);
}
