package net.createmod.metanip.render;

// RenderType system not available in 1.7.10
// In 1.7.10 rendering is done via GL11 directly
// This class acts as a stub providing GL state setup equivalents

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public abstract class PonderRenderTypes {

    // In 1.7.10 we just set GL state directly before rendering
    // outlineSolid: opaque colored rendering with entity lighting
    public static void beginOutlineSolid() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    // outlineTranslucent: translucent face rendering with texture
    public static void beginOutlineTranslucent(ResourceLocation texture, boolean cull) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        if (cull) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void beginFluid() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void end() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    // Stub methods for compatibility with Outliner calls
    public static Object outlineSolid()                                      { return null; }
    public static Object outlineTranslucent(ResourceLocation tex, boolean c) { return null; }
    public static Object fluid()                                              { return null; }
}
