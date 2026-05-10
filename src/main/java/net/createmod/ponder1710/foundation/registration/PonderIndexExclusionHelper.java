package net.createmod.ponder1710.foundation.registration;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.createmod.ponder1710.api.registration.IndexExclusionHelper;
import net.createmod.ponder1710.api.registration.PonderPlugin;

// import net.minecraft.world.item.BlockItem; // ItemBlock in 1.7.10
// import net.minecraft.world.item.Item; // different package in 1.7.10
// import net.minecraft.world.level.ItemLike; // not available in 1.7.10
// import net.minecraft.world.level.block.Block; // different package in 1.7.10
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class PonderIndexExclusionHelper implements IndexExclusionHelper {

    private final Stream.Builder<Predicate<Item>> exclusions = Stream.builder();

    public static Stream<Predicate<Item>> pluginToExclusions(PonderPlugin plugin) {
        PonderIndexExclusionHelper helper = new PonderIndexExclusionHelper();
        plugin.indexExclusions(helper);
        return helper.getExclusions();
    }

    public Stream<Predicate<Item>> getExclusions() {
        return exclusions.build();
    }

    @Override
    public IndexExclusionHelper exclude(Item item) {
        exclusions.add(i -> i == item);
        return this;
    }

    @Override
    public IndexExclusionHelper excludeItemVariants(Class<? extends Item> itemClazz, Item originalVariant) {
        exclusions.add(item -> {
            if (!itemClazz.isInstance(item))
                return false;
            return item != originalVariant;
        });
        return this;
    }

    @Override
    public IndexExclusionHelper excludeBlockVariants(Class<? extends Block> blockClazz, Block originalVariant) {
        exclusions.add(item -> {
            if (!(item instanceof ItemBlock))
                return false;
            Block block = ((ItemBlock) item).field_150939_a;
            if (!blockClazz.isInstance(block))
                return false;
            return block != originalVariant;
        });
        return this;
    }

    @Override
    public IndexExclusionHelper exclude(Predicate<Item> predicate) {
        exclusions.add(predicate);
        return this;
    }
}
