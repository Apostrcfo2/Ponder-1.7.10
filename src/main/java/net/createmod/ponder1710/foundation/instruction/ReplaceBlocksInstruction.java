package net.createmod.ponder1710.foundation.instruction;

import java.util.function.UnaryOperator;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

// import net.minecraft.world.level.block.Blocks; // different in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // not available in 1.7.10
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ReplaceBlocksInstruction extends WorldModifyInstruction {

    // TODO: BlockState -> Block + meta in 1.7.10
    // UnaryOperator<BlockState> -> using Block directly
    private final Block blockToUse;
    private final int meta;
    private final boolean replaceAir;
    private final boolean spawnParticles;

    public ReplaceBlocksInstruction(Selection selection, Block blockToUse, int meta, boolean replaceAir, boolean spawnParticles) {
        super(selection);
        this.blockToUse = blockToUse;
        this.meta = meta;
        this.replaceAir = replaceAir;
        this.spawnParticles = spawnParticles;
    }

    @Override
    protected void runModification(Selection selection, PonderScene scene) {
        // TODO: PonderLevel block access not implemented yet
        // Original used level.getBounds(), level.getBlockState(), level.setBlockAndUpdate()
        // In 1.7.10: world.getBlock(x,y,z), world.setBlock(x,y,z,block,meta,flags)
    }

    @Override
    protected boolean needsRedraw() {
        return true;
    }
}
