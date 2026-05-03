package net.createmod.ponder1710.foundation.element;

import net.createmod.ponder1710.api.element.PonderElement;

public abstract class PonderElementBase implements PonderElement {

    boolean visible = true;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
