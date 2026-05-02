package net.createmod.ponder1710.enums;

import net.createmod.ponder1710.config.CClient;

// TODO: NeoForge/Forge config system replaced with simple singleton in 1.7.10
// import net.neoforged.fml.config.ModConfig;
// import net.neoforged.neoforge.common.ModConfigSpec;
// import net.createmod.catnip.config.ConfigBase;

public class PonderConfig {

    private static CClient client;

    public static void init() {
        client = new CClient();
    }

    public static CClient client() {
        if (client == null)
            client = new CClient();
        return client;
    }
}
