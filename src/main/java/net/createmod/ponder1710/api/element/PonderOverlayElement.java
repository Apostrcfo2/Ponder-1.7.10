package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;


public interface PonderOverlayElement extends PonderElement {

    void render(PonderScene scene, PonderUI screen, float partialTicks);
}
