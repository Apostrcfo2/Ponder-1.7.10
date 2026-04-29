package net.createmod.ponder1710.api;

import net.createmod.ponder1710.api.level.PonderLevel;

@FunctionalInterface
public interface ParticleEmitter {
	void create(PonderLevel world, double x, double y, double z);
}
