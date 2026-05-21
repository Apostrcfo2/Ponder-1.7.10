package net.createmod.metanip.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public interface BindableTexture {

    default void bind() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(getLocation());
    }

    ResourceLocation getLocation();
}
