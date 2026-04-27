package su.sergiusonesimus.recreate.content.ponder.scenes;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.content.ponder.PonderScene;

@SideOnly(Side.CLIENT)
public class ShaftPonderScene extends PonderScene {

    private final ShaftModel model = new ShaftModel();

    @Override
    public void render(float partialTicks) {
        GL11.glPushMatrix();

        // Rotate scene for better view angle
        GL11.glRotatef(30, 1, 0, 0);
        GL11.glRotatef(45, 0, 1, 0);

        // Rotate shaft around Y axis
        model.setRotation((float) Math.toRadians(rotation));

        // Enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        model.render();

        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
