package net.createmod.ponder1710.foundation;

// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import net.createmod.metanip.gui.element.ScreenElement; // TODO: catnip not available
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

    public void render(int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(0.25f, 0.25f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(0,  64, 0, 0, 1);
        tess.addVertexWithUV(64, 64, 0, 1, 1);
        tess.addVertexWithUV(64, 0,  0, 1, 0);
        tess.addVertexWithUV(0,  0,  0, 0, 0);
        tess.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static PonderChapter of(ResourceLocation id) {
        return null;
    }
}
