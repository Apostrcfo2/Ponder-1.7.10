package net.createmod.ponder1710.enums;

import org.lwjgl.input.Keyboard;

// Ponder-specific keybinds - active ONLY when PonderUI is open.
// Uses same default keys as original Ponder (Q/E/S/A/D) but does NOT
// register them in the controls menu to avoid conflicts.
// These only fire via PonderUI.keyTyped() override.
public enum PonderKeybinds {

    IDENTIFY ("q",  Keyboard.KEY_Q),
    CLOSE    ("e",  Keyboard.KEY_E),
    REPLAY   ("s",  Keyboard.KEY_S),
    PREV     ("a",  Keyboard.KEY_A),
    NEXT     ("d",  Keyboard.KEY_D);

    private final String displayName;
    private final int keyCode;

    PonderKeybinds(String displayName, int keyCode) {
        this.displayName = displayName;
        this.keyCode     = keyCode;
    }

    public int getKeyCode() { return keyCode; }

    public String getKeyDescription() { return displayName; }

    public String message() { return displayName; }

    // Minimal stub so PonderButton.withShortcut() compiles
    private net.minecraft.client.settings.KeyBinding stub;

    public net.minecraft.client.settings.KeyBinding getMapping() {
        if (stub == null) {
            // Create a stub KeyBinding that is NOT registered anywhere
            stub = new net.minecraft.client.settings.KeyBinding(
                "key.ponder." + displayName, keyCode, "ponder"
            );
        }
        return stub;
    }

    public boolean isDown() { return org.lwjgl.input.Keyboard.isKeyDown(keyCode); }
}
