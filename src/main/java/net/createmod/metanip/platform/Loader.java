package net.createmod.metanip.platform;

// In 1.7.10 only Forge exists - no Fabric or NeoForge
public enum Loader {
    FORGE;

    public boolean isForge()    { return this == FORGE; }
    public boolean isFabric()   { return false; }
    public boolean isNeoForge() { return false; }

    public boolean isCurrent()  { return this == FORGE; }
}
