package net.createmod.metanip.gui.element;

import net.createmod.metanip.render.ColoredRenderable;
import net.createmod.metanip.theme.Color;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class GuiGameElement extends AbstractRenderElement {

    private ItemStack stack;
    private float scale = 1f;
    private int offsetX = 0, offsetY = 0;

    private GuiGameElement() {}

    public static GuiGameElement of(ItemStack stack) {
        GuiGameElement e = new GuiGameElement();
        e.stack = stack;
        return e;
    }

    public static GuiGameElement of(Item item) {
        return of(new ItemStack(item));
    }

    public static GuiGameElement of(Block block) {
        return of(new ItemStack(block));
    }

    public GuiGameElement scale(float scale) {
        this.scale = scale;
        return this;
    }

    public GuiGameElement at(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    @Override
    public void render() {
        if (stack == null) return;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + offsetX, y + offsetY, z);
        GL11.glScalef(scale, scale, scale);

        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(
            Minecraft.getMinecraft().fontRendererObj,
            Minecraft.getMinecraft().getTextureManager(),
            stack, 0, 0
        );
        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }
}
