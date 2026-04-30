package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;

// import net.minecraft.client.gui.GuiGraphics; // TODO: not available in 1.7.10 - use GL11

public interface PonderOverlayElement extends PonderElement {

    // TODO: GuiGraphics replaced with GL11 rendering in 1.7.10
    void render(PonderScene scene, PonderUI screen, float partialTicks);
}
