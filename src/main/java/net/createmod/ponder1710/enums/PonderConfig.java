package net.createmod.ponder1710.enums;

import net.createmod.ponder1710.config.CClient;

// import net.neoforged.fml.config.ModConfig;
// import net.neoforged.neoforge.common.ModConfigSpec;
// import net.createmod.metanip.config.ConfigBase;

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
