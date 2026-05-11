package net.createmod.ponder1710.foundation.registration;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.createmod.ponder1710.api.registration.TagBuilder;

// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10
// import net.minecraft.world.level.ItemLike; // not available in 1.7.10
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PonderTagBuilder implements TagBuilder {

    final ResourceLocation id;
    private final Consumer<PonderTagBuilder> onFinish;

    String title = "NO_TITLE";
    String description = "NO_DESCRIPTION";
    boolean addToIndex = false;
    @Nullable
    ResourceLocation textureIconLocation;
    ItemStack itemIcon = null;
    ItemStack mainItem = null;

    public PonderTagBuilder(ResourceLocation id, Consumer<PonderTagBuilder> onFinish) {
        this.id = id;
        this.onFinish = onFinish;
    }

    @Override
    public TagBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public TagBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public TagBuilder addToIndex() {
        this.addToIndex = true;
        return this;
    }

    @Override
    public TagBuilder icon(ResourceLocation location) {
        // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
        this.textureIconLocation = new ResourceLocation(location.getResourceDomain(),
            "textures/ponder/tag/" + location.getResourcePath() + ".png");
        return this;
    }

    @Override
    public TagBuilder icon(String path) {
        this.textureIconLocation = new ResourceLocation(id.getResourceDomain(),
            "textures/ponder/tag/" + path + ".png");
        return this;
    }

    @Override
    public TagBuilder idAsIcon() {
        return icon(id);
    }

    @Override
    // ItemLike -> Item in 1.7.10
    public TagBuilder item(Item item, boolean useAsIcon, boolean useAsMainItem) {
        if (useAsIcon)
            this.itemIcon = new ItemStack(item);
        if (useAsMainItem)
            this.mainItem = new ItemStack(item);
        return this;
    }

    @Override
    public void register() {
        onFinish.accept(this);
    }
}
