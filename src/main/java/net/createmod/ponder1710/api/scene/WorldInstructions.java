package net.createmod.ponder1710.api.scene;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.EntityElement;
import net.createmod.ponder1710.api.element.WorldSectionElement;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// BlockPos -> x,y,z ints in 1.7.10
// Direction -> ForgeDirection in 1.7.10
// BlockState -> Block + meta in 1.7.10
// BlockEntity -> TileEntity in 1.7.10
// CompoundTag -> NBTTagCompound in 1.7.10
// Level -> World in 1.7.10
// HolderLookup -> not available in 1.7.10
// modifyBlock/cycleBlockProperty/modifyBlocks -> adapted for Block+meta system
public interface WorldInstructions {

    void incrementBlockBreakingProgress(int x, int y, int z);

    void showSection(Selection selection, ForgeDirection fadeInDirection);

    void showSectionAndMerge(Selection selection, ForgeDirection fadeInDirection,
        ElementLink<WorldSectionElement> link);

    void glueBlockOnto(int x, int y, int z, ForgeDirection fadeInDirection,
        ElementLink<WorldSectionElement> link);

    ElementLink<WorldSectionElement> showIndependentSection(Selection selection,
        ForgeDirection fadeInDirection);

    ElementLink<WorldSectionElement> showIndependentSectionImmediately(Selection selection);

    void hideSection(Selection selection, ForgeDirection fadeOutDirection);

    void hideIndependentSection(ElementLink<WorldSectionElement> link,
        ForgeDirection fadeOutDirection);

    void restoreBlocks(Selection selection);

    ElementLink<WorldSectionElement> makeSectionIndependent(Selection selection);

    void rotateSection(ElementLink<WorldSectionElement> link, double xRotation,
        double yRotation, double zRotation, int duration);

    void configureCenterOfRotation(ElementLink<WorldSectionElement> link, Vec3 anchor);

    void configureStabilization(ElementLink<WorldSectionElement> link, Vec3 anchor);

    void moveSection(ElementLink<WorldSectionElement> link, Vec3 offset, int duration);

    void setBlocks(Selection selection, Block block, int meta, boolean spawnParticles);

    void destroyBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, Block block, int meta, boolean spawnParticles);

    void replaceBlocks(Selection selection, Block block, int meta, boolean spawnParticles);

    // modifyBlock - applies a function to block+meta at a position
    void modifyBlock(int x, int y, int z, UnaryOperator<int[]> blockMetaFunc, boolean spawnParticles);

    // cycleBlockProperty - cycles metadata value at position
    void cycleBlockProperty(int x, int y, int z);

    // modifyBlocks - applies a function to all blocks in selection
    void modifyBlocks(Selection selection, UnaryOperator<int[]> blockMetaFunc, boolean spawnParticles);

    void toggleRedstonePower(Selection selection);

    <T extends Entity> void modifyEntities(Class<T> entityClass, Consumer<T> entityCallBack);

    <T extends Entity> void modifyEntitiesInside(Class<T> entityClass, Selection area,
        Consumer<T> entityCallBack);

    void modifyEntity(ElementLink<EntityElement> link, Consumer<Entity> entityCallBack);

    ElementLink<EntityElement> createEntity(Function<World, Entity> factory);

    ElementLink<EntityElement> createItemEntity(Vec3 location, Vec3 motion, ItemStack stack);

    void modifyTileEntityNBT(Selection selection, Class<? extends TileEntity> teType,
        Consumer<NBTTagCompound> consumer);

    <T extends TileEntity> void modifyTileEntity(int x, int y, int z, Class<T> teType,
        Consumer<T> consumer);

    void modifyTileEntityNBT(Selection selection, Class<? extends TileEntity> teType,
        Consumer<NBTTagCompound> consumer, boolean reDrawBlocks);
}
