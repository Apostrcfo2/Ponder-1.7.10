package net.createmod.ponder1710.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientPacketListener;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
	@Accessor("serverChunkRadius")
	int catnip$getServerChunkRadius();
}
