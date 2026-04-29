package net.createmod.ponder1710.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("storageSource")
	LevelStorageSource.LevelStorageAccess catnip$getStorageSource();
}
