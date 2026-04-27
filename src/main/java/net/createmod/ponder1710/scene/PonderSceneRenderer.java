package net.createmod.ponder1710.scene;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PonderSceneRenderer {

    public static void render(PonderScene scene, float partialTicks) {
        GL11.glPushMatrix();

        // Apply camera rotation
        GL11.glRotatef(scene.getCameraPitch(), 1, 0, 0);
        GL11.glRotatef(scene.getCameraYaw(), 0, 1, 0);

        // Enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        // Render schematic blocks
        PonderSchematicRenderer.render(scene);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
