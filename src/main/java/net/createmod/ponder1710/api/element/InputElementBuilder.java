package net.createmod.ponder1710.api.element;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.world.item.ItemStack;

public interface InputElementBuilder {

	InputElementBuilder withItem(ItemStack stack);

	InputElementBuilder leftClick();

	InputElementBuilder rightClick();

	InputElementBuilder scroll();

	InputElementBuilder showing(ScreenElement icon);

	InputElementBuilder whileSneaking();

	InputElementBuilder whileCTRL();
}
