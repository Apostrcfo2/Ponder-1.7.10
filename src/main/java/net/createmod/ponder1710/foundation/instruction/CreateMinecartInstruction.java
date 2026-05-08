package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.MinecartElement;

// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
import net.minecraftforge.common.util.ForgeDirection;

public class CreateMinecartInstruction extends FadeIntoSceneInstruction<MinecartElement> {

    public CreateMinecartInstruction(int fadeInTicks, ForgeDirection fadeInFrom, MinecartElement element) {
        super(fadeInTicks, fadeInFrom, element);
    }

    @Override
    protected Class<MinecartElement> getElementClass() {
        return MinecartElement.class;
    }
}
