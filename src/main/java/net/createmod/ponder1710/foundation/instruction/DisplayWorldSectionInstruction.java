package net.createmod.ponder1710.foundation.instruction;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.element.WorldSectionElementImpl;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
import net.minecraftforge.common.util.ForgeDirection;

public class DisplayWorldSectionInstruction extends FadeIntoSceneInstruction<WorldSectionElement> {

    private final Selection initialSelection;
    @Nullable
    private final Supplier<WorldSectionElement> mergeOnto;
    // BlockPos -> int[] {x,y,z} in 1.7.10
    @Nullable
    private final int[] glue;

    public DisplayWorldSectionInstruction(int fadeInTicks, ForgeDirection fadeInFrom, Selection selection,
        @Nullable Supplier<WorldSectionElement> mergeOnto) {
        this(fadeInTicks, fadeInFrom, selection, mergeOnto, null);
    }

    public DisplayWorldSectionInstruction(int fadeInTicks, ForgeDirection fadeInFrom, Selection selection,
        @Nullable Supplier<WorldSectionElement> mergeOnto, @Nullable int[] glue) {
        super(fadeInTicks, fadeInFrom, new WorldSectionElementImpl(selection));
        initialSelection = selection;
        this.mergeOnto = mergeOnto;
        this.glue = glue;
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        Optional.ofNullable(mergeOnto).ifPresent(wse -> element.setAnimatedOffset(wse.get().getAnimatedOffset(), true));
        element.set(initialSelection);
        element.setVisible(true);
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (remainingTicks > 0)
            return;
        Optional.ofNullable(mergeOnto).ifPresent(c -> element.mergeOnto(c.get()));
    }

    @Override
    protected Class<WorldSectionElement> getElementClass() {
        return WorldSectionElement.class;
    }
}
