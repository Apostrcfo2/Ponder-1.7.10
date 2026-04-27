package net.createmod.ponder1710.ui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.createmod.ponder1710.instruction.TextInstruction;
import net.createmod.ponder1710.registry.PonderRegistry;
import net.createmod.ponder1710.scene.PonderScene;
import net.createmod.ponder1710.scene.PonderSceneRenderer;

@SideOnly(Side.CLIENT)
public class PonderUI extends GuiScreen {

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 240;

    private final List<PonderScene> scenes;
    private int currentScene = 0;
    private int panelX;
    private int panelY;

    public PonderUI(List<PonderScene> scenes) {
        this.scenes = scenes;
    }

    @Override
    public void initGui() {
        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Panel background
        drawRect(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xDD000000);
        drawRect(panelX + 1, panelY + 1, panelX + PANEL_WIDTH - 1, panelY + PANEL_HEIGHT - 1, 0xFF1a1a1a);

        PonderScene scene = scenes.get(currentScene);

        // Title
        fontRendererObj.drawStringWithShadow(scene.getTitle(), panelX + 10, panelY + 8, 0xFFFFFF);

        // Separator
        drawRect(panelX + 10, panelY + 20, panelX + PANEL_WIDTH - 10, panelY + 21, 0xFF444444);

        // Scene render area
        int renderX = panelX + PANEL_WIDTH / 2;
        int renderY = panelY + PANEL_HEIGHT / 2 - 10;

        GL11.glPushMatrix();
        GL11.glTranslatef(renderX, renderY, 100);
        GL11.glScalef(40, 40, 40);
        PonderSceneRenderer.render(scene, partialTicks);
        GL11.glPopMatrix();

        // Active text instruction
        for (int i = scene.getCurrentInstructionIndex(); i >= 0 && i < scene.getInstructions().size(); i--) {
            if (scene.getInstructions().get(i) instanceof TextInstruction text) {
                if (!text.isComplete()) {
                    drawRect(panelX + 10, panelY + PANEL_HEIGHT - 40,
                        panelX + PANEL_WIDTH - 10, panelY + PANEL_HEIGHT - 15, 0xAA000000);
                    fontRendererObj.drawSplitString(text.getText(),
                        panelX + 14, panelY + PANEL_HEIGHT - 37,
                        PANEL_WIDTH - 28, 0xFFFFFF);
                    break;
                }
            }
        }

        // Navigation
        if (scenes.size() > 1) {
            String nav = (currentScene + 1) + " / " + scenes.size();
            fontRendererObj.drawString(nav,
                panelX + PANEL_WIDTH / 2 - fontRendererObj.getStringWidth(nav) / 2,
                panelY + PANEL_HEIGHT - 10, 0x888888);
        }

        // Controls hint
        fontRendererObj.drawString("ESC close  |  R restart  |  A/D navigate",
            panelX + 10, panelY + PANEL_HEIGHT - 10, 0x555555);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        scenes.get(currentScene).tick();
    }

    @Override
    protected void keyTyped(char key, int keyCode) {
        // ESC
        if (keyCode == 1) mc.displayGuiScreen(null);
        // R - restart
        if (keyCode == 19) scenes.get(currentScene).reset();
        // A - previous scene
        if (keyCode == 30 && currentScene > 0) {
            currentScene--;
            scenes.get(currentScene).reset();
        }
        // D - next scene
        if (keyCode == 32 && currentScene < scenes.size() - 1) {
            currentScene++;
            scenes.get(currentScene).reset();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void open(ItemStack stack) {
        List<PonderScene> scenes = PonderRegistry.getScenes(stack);
        if (scenes != null && !scenes.isEmpty()) {
            Minecraft.getMinecraft().displayGuiScreen(new PonderUI(scenes));
        }
    }
}
