package net.createmod.metanip.client.render.model;

import com.mojang.blaze3d.vertex.MeshData;

import net.minecraft.client.renderer.RenderType;

public interface ShadeSeparatedResultConsumer {
	void accept(RenderType renderType, boolean shaded, MeshData data);
}
