package net.createmod.metanip.platform;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Env {
    CLIENT, SERVER;

    public boolean isClient() { return this == CLIENT; }
    public boolean isServer() { return this == SERVER; }

    public boolean isCurrent() {
        return this == (FMLLaunchHandler.side().isClient() ? CLIENT : SERVER);
    }
}
