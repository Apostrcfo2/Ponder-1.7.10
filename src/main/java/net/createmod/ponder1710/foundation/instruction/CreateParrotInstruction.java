package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.ParrotElement;
import net.minecraftforge.common.util.ForgeDirection;

// Note: ParrotElement uses EntityChicken as substitute in 1.7.10
public class CreateParrotInstruction extends FadeIntoSceneInstruction<ParrotElement> {

    public CreateParrotInstruction(int fadeInTicks, ForgeDirection fadeInFrom, ParrotElement element) {
        super(fadeInTicks, fadeInFrom, element);
    }

    @Override
    protected Class<ParrotElement> getElementClass() {
        return ParrotElement.class;
    }
}
