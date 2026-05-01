package net.createmod.ponder1710.api.registration;

import java.util.List;
import java.util.Set;

import net.createmod.ponder1710.foundation.PonderTag;
import net.minecraft.util.ResourceLocation;

public interface TagRegistryAccess {

    PonderTag getRegisteredTag(ResourceLocation tagLocation);

    List<PonderTag> getListedTags();

    Set<PonderTag> getTags(ResourceLocation item);

    Set<ResourceLocation> getItems(ResourceLocation tag);

    Set<ResourceLocation> getItems(PonderTag tag);
}
