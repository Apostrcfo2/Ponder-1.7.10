package net.createmod.ponder1710.api.registration;

// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different package
import net.minecraft.util.ResourceLocation;

public interface MultiTagBuilder {

    interface Tag<T> {
        Tag<T> add(T component);
    }

    interface Component {
        Component add(ResourceLocation tag);
    }
}
