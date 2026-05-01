package net.createmod.ponder1710.api.registration;

import java.util.function.BiConsumer;

// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different package
import net.minecraft.util.ResourceLocation;

public interface LangRegistryAccess {

    void provideLang(String modId, BiConsumer<String, String> consumer);

    String getShared(ResourceLocation key);

    String getShared(ResourceLocation key, Object... params);

    String getTagName(ResourceLocation key);

    String getTagDescription(ResourceLocation key);

    String getSpecific(ResourceLocation sceneId, String k);

    String getSpecific(ResourceLocation sceneId, String k, Object... params);
}
