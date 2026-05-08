package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.ParrotElement;

// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
import net.minecraftforge.common.util.ForgeDirection;

// TODO: Parrot not in 1.7.10 - this instruction will remain a stub
public class CreateParrotInstruction extends FadeIntoSceneInstruction<ParrotElement> {

    public CreateParrotInstruction(int fadeInTicks, ForgeDirection fadeInFrom, ParrotElement element) {
        super(fadeInTicks, fadeInFrom, element);
    }

    @Override
    protected Class<ParrotElement> getElementClass() {
        return ParrotElement.class;
    }
}
