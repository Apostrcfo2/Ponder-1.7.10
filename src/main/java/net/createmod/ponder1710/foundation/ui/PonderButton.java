package net.createmod.ponder1710.foundation.ui;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import net.createmod.metanip.animation.AnimationTickHolder; // TODO: catnip not available
// import net.createmod.metanip.animation.LerpedFloat; // TODO: catnip not available
// import net.createmod.metanip.data.Couple; // TODO: catnip not available
// import net.createmod.metanip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.metanip.gui.element.GuiGameElement; // TODO: catnip not available
// import net.createmod.metanip.gui.widget.BoxWidget; // TODO: catnip not available
// import net.createmod.metanip.theme.Color; // TODO: catnip not available
// import net.minecraft.client.KeyMapping; // KeyBinding in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10

import net.createmod.ponder1710.foundation.PonderTag;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

public class PonderButton extends GuiButton {

    // TODO: Color/Couple from catnip not available - using int colors
    public static final int COLOR_IDLE = 0x60c0c0ff;
    public static final int COLOR_HOVER = 0xf0c0c0ff;
    public static final int COLOR_CLICK = 0xffffffff;
    public static final int COLOR_DISABLED = 0x80909090;

    @Nullable
    protected ItemStack item;
    @Nullable
    protected PonderTag tag;
    @Nullable
    protected KeyBinding shortcut;

    // TODO: LerpedFloat from catnip not available
    protected float flashValue = 0;

    public PonderButton(int x, int y) {
        this(x, y, 20, 20);
    }

    public PonderButton(int x, int y, int width, int height) {
        super(0, x, y, width, height, "");
    }

    public PonderButton withShortcut(KeyBinding key) {
        this.shortcut = key;
        return this;
    }

    public PonderButton showingTag(PonderTag tag) {
        this.tag = tag;
        return this;
    }

    public PonderButton showing(ItemStack item) {
        this.item = item;
        return this;
    }

    public void flash() {
        flashValue = 1;
    }

    public void dim() {
        flashValue = 0;
    }

    @Nullable
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public PonderTag getTag() {
        return tag;
    }
}
