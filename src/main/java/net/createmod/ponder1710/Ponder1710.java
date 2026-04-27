package net.createmod.ponder1710;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.createmod.ponder1710.registry.PonderRegistry;

@Mod(modid = Ponder1710.MOD_ID, version = Ponder1710.VERSION, name = Ponder1710.NAME, acceptedMinecraftVersions = "[1.7.10]")
public class Ponder1710 {

    public static final String MOD_ID = "ponder1710";
    public static final String NAME = "Ponder 1.7.10";
    public static final String VERSION = "0.1.0";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Instance(MOD_ID)
    public static Ponder1710 instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Ponder 1.7.10 initializing...");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PonderRegistry.init();
        LOGGER.info("Ponder 1.7.10 initialized.");
    }
}
