package net.createmod.metanip.outliner;

import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class ItemOutline extends Outline {

    protected Vec3 pos;
    protected ItemStack stack;

    public ItemOutline(Vec3 pos, ItemStack stack) {
        this.pos = pos;
        this.stack = stack;
    }

    @Override
    public void render(Matrix4f ms, Vec3 camera, float pt) {
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();
        GL11.glTranslatef(
            (float)(pos.xCoord - camera.xCoord),
            (float)(pos.yCoord - camera.yCoord),
            (float)(pos.zCoord - camera.zCoord)
        );
        GL11.glScalef(params.alpha, params.alpha, params.alpha);

        // In 1.7.10 items are rendered via renderItem
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItem(stack, null);
        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
    }
}
