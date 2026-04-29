package net.createmod.ponder1710.api.scene;

import java.util.function.Consumer;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.instruction.PonderInstruction;

public interface DebugInstructions {
	void debugSchematic();

	void addInstructionInstance(PonderInstruction instruction);

	void enqueueCallback(Consumer<PonderScene> callback);
}
