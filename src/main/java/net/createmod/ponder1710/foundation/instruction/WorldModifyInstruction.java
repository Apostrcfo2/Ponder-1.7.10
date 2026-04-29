package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

public abstract class WorldModifyInstruction extends PonderInstruction {

	private final Selection selection;

	public WorldModifyInstruction(Selection selection) {
		this.selection = selection;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		runModification(selection, scene);
		if (needsRedraw())
			scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
	}

	protected abstract void runModification(Selection selection, PonderScene scene);

	protected abstract boolean needsRedraw();

}
