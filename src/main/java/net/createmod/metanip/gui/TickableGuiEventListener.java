package net.createmod.metanip.gui;

// import net.minecraft.client.gui.components.events.GuiEventListener; // not available in 1.7.10
// In 1.7.10 widgets handle their own ticking

public interface TickableGuiEventListener {
    void tick();
}
