package net.createmod.metanip.registry;

import javax.annotation.Nullable;

// import net.minecraft.core.Registry; // not available in 1.7.10
// import net.minecraft.core.registries.BuiltInRegistries; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.item.Item; // different package in 1.7.10
// import net.minecraft.world.item.Items; // different package in 1.7.10
// import net.minecraft.world.level.ItemLike; // not available in 1.7.10
// import net.minecraft.world.level.block.Block; // different package in 1.7.10
// import net.minecraft.world.level.block.Blocks; // different package in 1.7.10

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.GameData;

public class RegisteredObjectsHelper {

    public static ResourceLocation getKeyOrThrow(Block block) {
        // GameData is the Forge registry in 1.7.10
        String name = GameData.getBlockRegistry().getNameForObject(block).toString();
        if (name == null)
            throw new IllegalArgumentException("Could not get key for block " + block + "!");
        return new ResourceLocation(name);
    }

    public static ResourceLocation getKeyOrThrow(Item item) {
        String name = GameData.getItemRegistry().getNameForObject(item).toString();
        if (name == null)
            throw new IllegalArgumentException("Could not get key for item " + item + "!");
        return new ResourceLocation(name);
    }

    public static Item getItem(ResourceLocation location) {
        return (Item) GameData.getItemRegistry().getObject(location.toString());
    }

    public static Block getBlock(ResourceLocation location) {
        return (Block) GameData.getBlockRegistry().getObject(location.toString());
    }

    @Nullable
    public static Object getItemOrBlock(ResourceLocation location) {
        Item item = getItem(location);
        if (item != null)
            return item;

        Block block = getBlock(location);
        if (block != null)
            return block;

        return null;
    }

    public static ResourceLocation getKeyOrThrow(Object itemOrBlock) {
        if (itemOrBlock instanceof Item)
            return getKeyOrThrow((Item) itemOrBlock);
        if (itemOrBlock instanceof Block)
            return getKeyOrThrow((Block) itemOrBlock);
        throw new IllegalArgumentException("Could not get key for " + itemOrBlock + "!");
    }
}
