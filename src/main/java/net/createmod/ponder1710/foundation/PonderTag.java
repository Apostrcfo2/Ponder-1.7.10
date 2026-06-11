package net.createmod.ponder1710.foundation;

import javax.annotation.Nullable;

import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.ponder1710.Ponder;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PonderTag {

    public static final class Highlight {
        public static final ResourceLocation ALL = Ponder.asResource("_all");
    }

    private final ResourceLocation id;
    @Nullable
    private final ResourceLocation textureIconLocation;
    private final ItemStack itemIcon;
    private final ItemStack mainItem;

    public PonderTag(ResourceLocation id, @Nullable ResourceLocation textureIconLocation,
        ItemStack itemIcon, ItemStack mainItem) {
        this.id = id;
        this.textureIconLocation = textureIconLocation;
        this.itemIcon = itemIcon;
        this.mainItem = mainItem;
    }

    public ResourceLocation getId()     { return id; }
    public ItemStack getMainItem()      { return mainItem; }
    public String getTitle()            { return PonderIndex.getLangAccess().getTagName(id); }
    public String getDescription()      { return PonderIndex.getLangAccess().getTagDescription(id); }

    public void render(int x, int y) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);

        if (textureIconLocation != null) {
            // Render texture icon
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureIconLocation);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1, 1, 1, 1);
            // Draw 16x16 quad
            net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
            tess.startDrawingQuads();
            tess.addVertexWithUV(0,  16, 0, 0, 1);
            tess.addVertexWithUV(16, 16, 0, 1, 1);
            tess.addVertexWithUV(16, 0,  0, 1, 0);
            tess.addVertexWithUV(0,  0,  0, 0, 0);
            tess.draw();
            GL11.glDisable(GL11.GL_BLEND);
        } else if (itemIcon != null && itemIcon.getItem() != null) {
            // Render item icon using GuiGameElement
            GuiGameElement.of(itemIcon)
                .at(0, 0)
                .render();
        }

        GL11.glPopMatrix();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PonderTag)) return false;
        return getId().equals(((PonderTag) other).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
