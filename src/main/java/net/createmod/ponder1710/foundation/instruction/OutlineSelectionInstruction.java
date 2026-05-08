package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

public class OutlineSelectionInstruction extends TickingInstruction {

    private final PonderPalette color;
    private final Object slot;
    private final Selection selection;

    public OutlineSelectionInstruction(PonderPalette color, Object slot, Selection selection, int ticks) {
        super(false, ticks);
        this.color = color;
        this.slot = slot;
        this.selection = selection;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        // TODO: selection.makeOutline - Outliner from catnip not available in 1.7.10
        // selection.makeOutline(scene.getOutliner(), slot).lineWidth(1/16f).colored(color.getColor());
    }
}
