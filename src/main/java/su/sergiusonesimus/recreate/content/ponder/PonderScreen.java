package su.sergiusonesimus.recreate.content.ponder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PonderScreen extends GuiScreen {

    private ItemStack subject;
    private PonderScene scene;
    private float ticks = 0;

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 200;

    public PonderScreen(ItemStack subject) {
        this.subject = subject;
        this.scene = PonderRegistry.getScene(subject);
    }

    @Override
    public void initGui() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int panelX = (width - PANEL_WIDTH) / 2;
        int panelY = (height - PANEL_HEIGHT) / 2;

        // Draw panel background
        drawRect(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xCC000000);
        drawRect(panelX + 1, panelY + 1, panelX + PANEL_WIDTH - 1, panelY + PANEL_HEIGHT - 1, 0xFF1a1a1a);

        // Draw title
        String title = subject != null ? subject.getDisplayName() : "Ponder";
        fontRendererObj.drawString(title, panelX + 10, panelY + 10, 0xFFFFFF);

        // Draw separator
        drawRect(panelX + 10, panelY + 22, panelX + PANEL_WIDTH - 10, panelY + 23, 0xFF555555);

        // Render scene
        if (scene != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(panelX + PANEL_WIDTH / 2, panelY + PANEL_HEIGHT / 2, 100);
            GL11.glScalef(40, 40, 40);
            scene.render(ticks + partialTicks);
            GL11.glPopMatrix();
        } else {
            fontRendererObj.drawString("No scene available.", panelX + 10, panelY + 40, 0xAAAAAA);
        }

        // Draw close hint
        fontRendererObj.drawString("Press ESC to close", panelX + 10, panelY + PANEL_HEIGHT - 15, 0x888888);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        ticks++;
        if (scene != null) scene.tick();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void open(ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen(new PonderScreen(stack));
    }
}
