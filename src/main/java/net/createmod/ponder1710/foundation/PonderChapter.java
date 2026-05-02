package net.createmod.ponder1710.foundation;

// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import net.createmod.catnip.gui.element.ScreenElement; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderChapter {

    private final ResourceLocation id;
    private final ResourceLocation icon;

    private PonderChapter(ResourceLocation id) {
        this.id = id;
        // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
        icon = new ResourceLocation(id.getResourceDomain(), "textures/ponder/chapter/" + id.getResourcePath() + ".png");
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getTitle() {
        return "";
    }

    // TODO: render(GuiGraphics, int, int) - GuiGraphics not available in 1.7.10
    public void render(int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(0.25f, 0.25f, 1f);
        // TODO: blit equivalent in 1.7.10
        GL11.glPopMatrix();
    }

    @Deprecated
    public static PonderChapter of(ResourceLocation id) {
        return null;
    }
}
