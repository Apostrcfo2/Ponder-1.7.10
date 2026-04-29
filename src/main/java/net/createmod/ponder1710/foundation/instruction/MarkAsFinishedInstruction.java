package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.foundation.PonderScene;

public class MarkAsFinishedInstruction extends PonderInstruction {

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public void tick(PonderScene scene) {
		scene.setFinished(true);
	}

	@Override
	public void onScheduled(PonderScene scene) {
		scene.stopCounting();
	}

}
