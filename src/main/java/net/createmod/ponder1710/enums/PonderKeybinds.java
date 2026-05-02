package net.createmod.ponder1710.enums;

// import org.lwjgl.glfw.GLFW; // GLFW not available in 1.7.10 - uses LWJGL 2
// import net.createmod.catnip.client.ConflictSafeKeyMapping; // TODO: catnip not available
// import net.createmod.catnip.platform.CatnipClientServices; // TODO: catnip not available
// import net.minecraft.client.KeyMapping; // KeyBinding in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;

public enum PonderKeybinds {

    PONDER("ponder", Keyboard.KEY_W);

    public static final String CATEGORY = "key.categories.ponder";

    private final KeyBinding mapping;

    PonderKeybinds(String description, int defaultKey) {
        // KeyBinding in 1.7.10
        this.mapping = new KeyBinding("key.ponder." + description, defaultKey, CATEGORY);
    }

    public static void register() {
        for (PonderKeybinds key : values()) {
            ClientRegistry.registerKeyBinding(key.mapping);
        }
    }

    public boolean isDown() {
        return mapping.getIsKeyPressed();
    }

    public KeyBinding getMapping() {
        return mapping;
    }
}
