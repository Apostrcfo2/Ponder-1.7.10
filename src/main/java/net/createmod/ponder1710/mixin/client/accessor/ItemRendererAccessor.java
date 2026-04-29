package net.createmod.ponder1710.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
	@Accessor("textureManager")
	TextureManager catnip$getTextureManager();
}
