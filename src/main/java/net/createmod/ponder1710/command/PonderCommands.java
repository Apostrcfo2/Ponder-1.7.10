package net.createmod.ponder1710.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

// 1.7.10 commands registered via FMLServerStartingEvent
public class PonderCommands {

    public static void register(FMLServerStartingEvent event) {
        event.registerServerCommand(new PonderCommand());
    }
}
