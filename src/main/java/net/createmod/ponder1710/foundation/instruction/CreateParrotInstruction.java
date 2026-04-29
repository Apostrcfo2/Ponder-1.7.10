package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.ParrotElement;
import net.minecraft.core.Direction;

public class CreateParrotInstruction extends FadeIntoSceneInstruction<ParrotElement> {

	public CreateParrotInstruction(int fadeInTicks, Direction fadeInFrom, ParrotElement element) {
		super(fadeInTicks, fadeInFrom, element);
	}

	@Override
	protected Class<ParrotElement> getElementClass() {
		return ParrotElement.class;
	}

}
