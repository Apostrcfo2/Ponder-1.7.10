package net.createmod.ponder1710.foundation.instruction;

import java.util.function.UnaryOperator;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class BlockEntityDataInstruction extends WorldModifyInstruction {

    private final boolean redraw;
    private final UnaryOperator<NBTTagCompound> data;
    private final Class<? extends TileEntity> type;

    public BlockEntityDataInstruction(Selection selection, Class<? extends TileEntity> type,
        UnaryOperator<NBTTagCompound> data, boolean redraw) {
        super(selection);
        this.type = type;
        this.data = data;
        this.redraw = redraw;
    }

    @Override
    protected void runModification(Selection selection, PonderScene scene) {
        PonderLevel world = scene.getWorld();

        selection.forEach(pos -> {
            int x = pos[0], y = pos[1], z = pos[2];
            TileEntity te = world.getTileEntity(x, y, z);
            if (te == null) return;
            if (!type.isInstance(te)) return;

            // 1.7.10: writeToNBT + readFromNBT
            NBTTagCompound tag = new NBTTagCompound();
            te.writeToNBT(tag);
            tag = data.apply(tag);
            te.readFromNBT(tag);
            world.markBlockForUpdate(x, y, z);
        });
    }

    @Override
    protected boolean needsRedraw() {
        return redraw;
    }
}
