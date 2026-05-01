package net.createmod.ponder1710.api.registration;

import java.util.function.Predicate;

// import net.minecraft.world.item.Item; // 1.7.10 uses different package
// import net.minecraft.world.level.ItemLike; // TODO: not available in 1.7.10
// import net.minecraft.world.level.block.Block; // 1.7.10 uses different package
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IndexExclusionHelper {

    // ItemLike -> Item in 1.7.10
    IndexExclusionHelper exclude(Item item);

    IndexExclusionHelper excludeItemVariants(Class<? extends Item> itemClazz, Item originalVariant);

    IndexExclusionHelper excludeBlockVariants(Class<? extends Block> blockClazz, Block originalVariant);

    IndexExclusionHelper exclude(Predicate<Item> predicate);
}
