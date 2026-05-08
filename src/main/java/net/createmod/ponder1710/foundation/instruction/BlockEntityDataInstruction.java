package net.createmod.ponder1710.foundation.instruction;

import java.util.function.UnaryOperator;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

// import net.minecraft.nbt.CompoundTag; // NBTTagCompound in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntity; // TileEntity in 1.7.10
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
        // TODO: PonderLevel.getBounds() not available yet
        // TODO: TileEntity NBT system different in 1.7.10
        // Original used blockEntity.saveWithFullMetadata and loadWithComponents
        // In 1.7.10: tileEntity.writeToNBT(tag) and tileEntity.readFromNBT(tag)
    }

    @Override
    protected boolean needsRedraw() {
        return redraw;
    }
}
