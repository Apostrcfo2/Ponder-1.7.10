package net.createmod.ponder1710.api.element;

import java.util.UUID;

public interface ElementLink<T extends PonderElement> {
	UUID getId();

	T cast(PonderElement e);
}
