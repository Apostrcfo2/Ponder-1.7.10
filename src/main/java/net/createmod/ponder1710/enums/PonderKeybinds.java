package net.createmod.ponder1710.enums;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;

public enum PonderKeybinds {

    PONDER("ponder", Keyboard.KEY_W);

    public static final String CATEGORY = "key.categories.ponder";

    private final KeyBinding mapping;

    PonderKeybinds(String description, int defaultKey) {
        this.mapping = new KeyBinding("key.ponder." + description, defaultKey, CATEGORY);
    }

    public static void register() {
        for (PonderKeybinds key : values())
            ClientRegistry.registerKeyBinding(key.mapping);
    }

    // Primary check - is the key currently held down
    public boolean isDown() {
        return mapping.getIsKeyPressed();
    }

    // Alias used by some components
    public boolean isKeyDown() {
        return isDown();
    }

    // Human-readable key name for tooltips
    public String getKeyDescription() {
        return KeyBinding.getKeyDisplayString(mapping.getKeyCode());
    }

    // Component equivalent for text display
    public String message() {
        return getKeyDescription();
    }

    public KeyBinding getMapping() {
        return mapping;
    }
}
