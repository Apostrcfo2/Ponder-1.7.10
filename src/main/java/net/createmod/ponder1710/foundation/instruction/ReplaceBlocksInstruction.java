package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ReplaceBlocksInstruction extends WorldModifyInstruction {

    private final Block blockToUse;
    private final int meta;
    private final boolean replaceAir;
    private final boolean spawnParticles;

    public ReplaceBlocksInstruction(Selection selection, Block blockToUse, int meta,
        boolean replaceAir, boolean spawnParticles) {
        super(selection);
        this.blockToUse = blockToUse;
        this.meta = meta;
        this.replaceAir = replaceAir;
        this.spawnParticles = spawnParticles;
    }

    @Override
    protected void runModification(Selection selection, PonderScene scene) {
        PonderLevel world = scene.getWorld();
        int[] bounds = world.getBounds();

        selection.forEach(pos -> {
            int x = pos[0], y = pos[1], z = pos[2];
            Block existing = world.getBlock(x, y, z);
            if (!replaceAir && existing == Blocks.air) return;

            if (spawnParticles) {
                world.addBlockDestroyEffects(x, y, z, existing, world.getBlockMetadata(x, y, z));
            }

            world.setBlock(x, y, z, blockToUse, meta, 3);
            world.markBlockForUpdate(x, y, z);
        });
    }

    @Override
    protected boolean needsRedraw() {
        return true;
    }
}
