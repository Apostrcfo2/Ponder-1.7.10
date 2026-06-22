package net.createmod.ponder1710.foundation.ui;

import java.util.Locale;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.data.Couple;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.gui.element.GuiGameElement;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.createmod.metanip.theme.Color;

import net.createmod.ponder1710.enums.PonderGuiTextures;
import net.createmod.ponder1710.foundation.PonderTag;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

public class PonderButton extends BoxWidget {

    public static final Couple<Color> COLOR_IDLE = Couple.create(
        new Color(0x60_c0c0ff, true),
        new Color(0x30_c0c0ff, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_HOVER = Couple.create(
        new Color(0xf0_c0c0ff, true),
        new Color(0xa0_c0c0ff, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_CLICK = Couple.create(
        new Color(0xff_ffffff, true),
        new Color(0xdd_ffffff, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_DISABLED = Couple.create(
        new Color(0x80_909090, true),
        new Color(0x20_909090, true)
    ).map(Color::setImmutable);

    @Nullable
    protected ItemStack item;
    @Nullable
    protected PonderTag tag;
    @Nullable
    protected PonderGuiTextures icon;
    @Nullable
    protected KeyBinding shortcut;
    protected LerpedFloat flash = LerpedFloat.linear().startWithValue(0).chase(0, 0.1f, LerpedFloat.Chaser.EXP);

    public PonderButton(int x, int y) {
        this(x, y, 20, 20);
    }

    public PonderButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        z = 420;
        paddingX = 2;
        paddingY = 2;
        colorIdle = COLOR_IDLE;
        colorHover = COLOR_HOVER;
        colorClick = COLOR_CLICK;
        colorDisabled = COLOR_DISABLED;
        updateGradientFromState();
    }

    public <T extends PonderButton> T withShortcut(KeyBinding key) {
        this.shortcut = key;
        //noinspection unchecked
        return (T) this;
    }

    public <T extends PonderButton> T showingTag(PonderTag tag) {
        this.tag = tag;
        this.item = tag.getMainItem();
        //noinspection unchecked
        return (T) this;
    }

    public <T extends PonderButton> T showing(ItemStack item) {
        this.item = item;
        //noinspection unchecked
        return (T) this;
    }

    public <T extends PonderButton> T showing(PonderGuiTextures icon) {
        this.icon = icon;
        //noinspection unchecked
        return (T) this;
    }

    public void flash() {
        flash.updateChaseTarget(1);
    }

    public void dim() {
        flash.updateChaseTarget(0);
    }

    public void atZLevel(float z) {
        this.z = z;
    }

    @Override
    public void tick() {
        super.tick();
        flash.tickChaser();
    }

    @Override
    protected void doRender(int mouseX, int mouseY, float partialTicks) {
        // Boost gradient brightness when flashing
        float flashValue = flash.getValue(partialTicks);
        updateGradientFromState();
        if (flashValue > .1f) {
            float sin = 0.5f + 0.5f * MathHelper.sin((AnimationTickHolder.getTicks() + partialTicks) / 10f);
            sin *= flashValue;
            Color nc1 = new Color(255, 255, 255, MathHelper.clamp_int(gradientColor.getFirst().getAlpha() + 150, 0, 255));
            Color nc2 = new Color(155, 155, 155, MathHelper.clamp_int(gradientColor.getSecond().getAlpha() + 150, 0, 255));
            Couple<Color> newColors = Couple.create(nc1, nc2);
            float finalSin = sin;
            gradientColor = gradientColor.mapWithParams((color, other) -> color.mixWith(other, finalSin), newColors);
        }

        super.doRender(mouseX, mouseY, partialTicks);

        if (!isVisible()) return;

        // Render item or texture icon centered on the button
        GL11.glPushMatrix();
        GL11.glTranslatef(xPosition + width / 2f - 8, yPosition + height / 2f - 8, z + 1);
        if (item != null && item.getItem() != null) {
            GuiGameElement.of(item).scale(1.5f).at(-4, -4).render();
        } else if (icon != null) {
            icon.render(0, 0);
        }
        GL11.glPopMatrix();

        if (shortcut != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, z + 10);
            String key = shortcut.getKeyDescription().toLowerCase(Locale.ROOT);
            int color = UIRenderHelper.COLOR_TEXT_DARKER.getFirst().scaleAlpha(fade.getValue()).getRGB();
            int strW = Minecraft.getMinecraft().fontRendererObj.getStringWidth(key);
            Minecraft.getMinecraft().fontRendererObj.drawString(key,
                xPosition + width / 2 + 8 - strW / 2,
                yPosition + height - 6, color);
            GL11.glPopMatrix();
        }
    }

    public boolean keyPressed(int keyCode) {
        if (shortcut != null && shortcut.getKeyCode() == keyCode) {
            gradientColor = getColorClick();
            startGradientAnimation(getColorForState(), 0.15);
            runCallback(width / 2f, height / 2f);
            return true;
        }
        return false;
    }

    public boolean isHoveredOrFocused() {
        return isHovered;
    }

    public int getX() { return xPosition; }
    public int getY() { return yPosition; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Nullable
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public PonderTag getTag() {
        return tag;
    }

    public boolean isVisible() {
        return !(fade.getValue() < .1f);
    }
}
