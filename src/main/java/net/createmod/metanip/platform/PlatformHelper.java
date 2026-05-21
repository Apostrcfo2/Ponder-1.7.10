package net.createmod.metanip.platform;

// Simplified PlatformHelper for 1.7.10 - no ServiceLoader needed
public interface PlatformHelper {
    Env getEnv();
    Loader getLoader();
    String getModDisplayName(String modId);
}
