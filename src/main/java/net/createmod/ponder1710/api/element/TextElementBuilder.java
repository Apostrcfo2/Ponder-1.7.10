package net.createmod.ponder1710.api.element;

import net.createmod.ponder1710.api.PonderPalette;

// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different package
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public interface TextElementBuilder {

    TextElementBuilder colored(PonderPalette color);

    TextElementBuilder pointAt(Vec3 vec);

    TextElementBuilder independent(int y);

    default TextElementBuilder independent() {
        return independent(0);
    }

    TextElementBuilder text(String defaultText);

    TextElementBuilder text(String defaultText, Object... params);

    TextElementBuilder sharedText(ResourceLocation key);

    TextElementBuilder sharedText(ResourceLocation key, Object... params);

    TextElementBuilder sharedText(String key);

    TextElementBuilder sharedText(String key, Object... params);

    TextElementBuilder placeNearTarget();

    TextElementBuilder attachKeyFrame();
}
