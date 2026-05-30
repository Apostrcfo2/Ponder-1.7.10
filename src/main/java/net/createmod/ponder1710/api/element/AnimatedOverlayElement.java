package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;


public interface AnimatedOverlayElement extends PonderOverlayElement {

    void setFade(float fade);

    float getFade(float partialTicks);

    @Override
    default void render(PonderScene scene, PonderUI screen, /* GuiGraphics graphics, */ float partialTicks) {
        render(scene, screen, partialTicks, getFade(partialTicks));
    }

    void render(PonderScene scene, PonderUI screen, /* GuiGraphics graphics, */ float partialTicks, float fade);
}
