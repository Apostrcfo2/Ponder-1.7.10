package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.foundation.PonderScene;

public interface PonderElement {
	default void whileSkipping(PonderScene scene) {
	}

	default void tick(PonderScene scene) {
	}

	default void reset(PonderScene scene) {
	}

	boolean isVisible();

	void setVisible(boolean visible);
}
