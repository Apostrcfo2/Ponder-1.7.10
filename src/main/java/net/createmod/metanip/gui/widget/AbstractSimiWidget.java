package net.createmod.metanip.gui.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import net.createmod.metanip.data.Couple;
import net.createmod.metanip.theme.Color;
import net.minecraft.client.gui.GuiButton;

public abstract class AbstractSimiWidget extends GuiButton {

    public static final Color HEADER_RGB = new Color(0x5391e1, false);
    public static final Color HINT_RGB = new Color(0x96b7e0, false);

    public static final Couple<Color> COLOR_IDLE = Couple.create(
        new Color(0xdd_8ab6d6, true), new Color(0x90_8ab6d6, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_HOVER = Couple.create(
        new Color(0xff_9abbd3, true), new Color(0xd0_9abbd3, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_CLICK = Couple.create(
        new Color(0xff_ffffff, true), new Color(0xee_ffffff, true)
    ).map(Color::setImmutable);
    public static final Couple<Color> COLOR_DISABLED = Couple.create(
        new Color(0x80_909090, true), new Color(0x60_909090, true)
    ).map(Color::setImmutable);

    protected float z;
    protected boolean wasHovered = false;
    protected List<String> toolTip = new LinkedList<>();
    protected BiConsumer<Integer, Integer> onClick = (_$, _$$) -> {};
    protected float paddingX = 2, paddingY = 2;

    protected AbstractSimiWidget(int x, int y, int width, int height) {
        super(0, x, y, width, height, "");
    }

    public <T extends AbstractSimiWidget> T withCallback(BiConsumer<Integer, Integer> cb) {
        this.onClick = cb;
        return (T) this;
    }

    public <T extends AbstractSimiWidget> T withCallback(Runnable cb) {
        return withCallback((_$, _$$) -> cb.run());
    }

    public <T extends AbstractSimiWidget> T setActive(boolean active) {
        this.enabled = active;
        return (T) this;
    }

    public void runCallback(double mouseX, double mouseY) {
        onClick.accept((int) mouseX, (int) mouseY);
    }

    @Override
    public boolean mousePressed(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
        if (!enabled || !visible) return false;
        boolean hovered = mouseX >= xPosition && mouseY >= yPosition
            && mouseX < xPosition + width && mouseY < yPosition + height;
        if (hovered) {
            runCallback(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            isHovered = mouseX >= xPosition && mouseY >= yPosition
                && mouseX < xPosition + width && mouseY < yPosition + height;
            doRender(mouseX, mouseY, 0);
            wasHovered = isHovered;
        }
    }

    protected void doRender(int mouseX, int mouseY, float partialTicks) {}
}
