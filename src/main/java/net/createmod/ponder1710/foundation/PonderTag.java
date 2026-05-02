package net.createmod.ponder1710.foundation;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.catnip.gui.element.GuiGameElement; // TODO: catnip not available
// import net.createmod.catnip.gui.element.ScreenElement; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10

import net.createmod.ponder1710.Ponder;
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

    public PonderTag(ResourceLocation id, @Nullable ResourceLocation textureIconLocation, ItemStack itemIcon, ItemStack mainItem) {
        this.id = id;
        this.textureIconLocation = textureIconLocation;
        this.itemIcon = itemIcon;
        this.mainItem = mainItem;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ItemStack getMainItem() {
        return mainItem;
    }

    public String getTitle() {
        return PonderIndex.getLangAccess().getTagName(id);
    }

    public String getDescription() {
        return PonderIndex.getLangAccess().getTagDescription(id);
    }

    // TODO: render(GuiGraphics, int, int) - GuiGraphics not available in 1.7.10
    public void render(int x, int y) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        if (textureIconLocation != null) {
            // TODO: blit equivalent using GL11
            GL11.glScalef(0.25f, 0.25f, 1f);
        } else if (itemIcon != null && !itemIcon.func_190926_b()) {
            // TODO: render item in 1.7.10
        }
        GL11.glPopMatrix();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof PonderTag))
            return false;
        PonderTag otherTag = (PonderTag) other;
        return getId().equals(otherTag.getId());
    }
}
