package net.createmod.metanip.platform.services;

import org.jetbrains.annotations.Nullable;

import net.createmod.metanip.annotations.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

public interface ModFluidHelper<R> {
	@ClientOnly
	int getColor(R fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos);

	int getLuminosity(R fluid);

	@ClientOnly
	@Nullable
	TextureAtlasSprite getStillTexture(R fluid);

	@ClientOnly
	default TextureAtlasSprite getStillTextureOrMissing(R fluid) {
		TextureAtlasSprite texture = this.getStillTexture(fluid);
		if (texture != null)
			return texture;

		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
	}

	boolean isLighterThanAir(R fluid);

	R toStack(FluidState state);
}
