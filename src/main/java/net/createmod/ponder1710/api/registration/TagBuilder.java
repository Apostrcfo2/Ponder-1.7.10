package net.createmod.ponder1710.api.registration;

import net.minecraft.util.ResourceLocation;

import net.minecraft.item.Item;

public interface TagBuilder {

    TagBuilder title(String title);

    TagBuilder description(String description);

    TagBuilder addToIndex();

    TagBuilder icon(ResourceLocation location);

    TagBuilder icon(String path);

    TagBuilder idAsIcon();

    // ItemLike -> Item in 1.7.10
    TagBuilder item(Item item, boolean useAsIcon, boolean useAsMainItem);

    default TagBuilder item(Item item) {
        return item(item, true, true);
    }

    void register();
}
