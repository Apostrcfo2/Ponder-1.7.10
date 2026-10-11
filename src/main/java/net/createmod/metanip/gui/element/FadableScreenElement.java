package net.createmod.metanip.gui.element;

// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10

@FunctionalInterface
public interface FadableScreenElement extends ScreenElement {

    @Override
    default void render(int x, int y) {
        render(x, y, 1f);
    }

    void render(int x, int y, float alpha);
}
