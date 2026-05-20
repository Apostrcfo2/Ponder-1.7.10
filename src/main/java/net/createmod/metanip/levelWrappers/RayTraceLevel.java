package net.createmod.metanip.levelWrappers;

import java.util.function.BiFunction;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.world.level.BlockGetter; // not available in 1.7.10
// import net.minecraft.world.level.LevelAccessor; // not available in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntity; // TileEntity in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // Block+meta in 1.7.10
// import net.minecraft.world.level.material.FluidState; // not available in 1.7.10

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

// In 1.7.10: Block+meta replaces BlockState, TileEntity replaces BlockEntity
// BiFunction<int[], Block, Block> replaces BiFunction<BlockPos, BlockState, BlockState>
public class RayTraceLevel {

    private final SchematicLevel template;
    // stateGetter: given pos {x,y,z} and original Block, returns replacement Block
    private final BiFunction<int[], Block, Block> blockGetter;

    public RayTraceLevel(SchematicLevel template, BiFunction<int[], Block, Block> blockGetter) {
        this.template = template;
        this.blockGetter = blockGetter;
    }

    public TileEntity getTileEntity(int x, int y, int z) {
        return template.getTileEntity(x, y, z);
    }

    public Block getBlock(int x, int y, int z) {
        Block original = template.getBlock(x, y, z);
        return blockGetter.apply(new int[]{x, y, z}, original);
    }

    public int getBlockMeta(int x, int y, int z) {
        return template.getBlockMeta(x, y, z);
    }

    public int getHeight() {
        return 256; // standard 1.7.10 world height
    }

    public int getMinBuildHeight() {
        return 0; // 1.7.10 starts at 0
    }
}
