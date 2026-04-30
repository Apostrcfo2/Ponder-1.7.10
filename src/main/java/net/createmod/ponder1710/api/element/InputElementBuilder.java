package net.createmod.ponder1710.api.element;

// import net.createmod.catnip.gui.element.ScreenElement; // TODO: catnip not available in 1.7.10
// import net.minecraft.world.item.ItemStack; // 1.7.10 uses different package
import net.minecraft.item.ItemStack;

public interface InputElementBuilder {

    InputElementBuilder withItem(ItemStack stack);

    InputElementBuilder leftClick();

    InputElementBuilder rightClick();

    InputElementBuilder scroll();

    // TODO: ScreenElement from catnip not available in 1.7.10
    // InputElementBuilder showing(ScreenElement icon);

    InputElementBuilder whileSneaking();

    InputElementBuilder whileCTRL();
}
