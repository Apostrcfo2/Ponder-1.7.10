package net.createmod.metanip.data;
// Pure Java - no changes needed for 1.7.10

public enum TriState {
    TRUE, DEFAULT, FALSE;

    public boolean isTrue() { return this == TRUE; }
    public boolean isDefault() { return this == DEFAULT; }
    public boolean isFalse() { return this == FALSE; }

    public boolean getValue() {
        if (this == TRUE) return true;
        if (this == FALSE) return false;
        throw new IllegalArgumentException("Default does not have a value");
    }
}
