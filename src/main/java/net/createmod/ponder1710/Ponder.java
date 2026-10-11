package net.createmod.ponder1710;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.createmod.metanip.lang.LangBuilder;
import net.minecraft.util.ResourceLocation;

@Mod(modid = Ponder.MOD_ID, name = Ponder.MOD_NAME, version = "0.1.0")
public class Ponder {

    public static final String MOD_ID   = "ponder1710";
    public static final String MOD_NAME = "Ponder 1.7.10";
    public static final Logger LOGGER   = LogManager.getLogger(MOD_NAME);
    public static final Random RANDOM   = new Random();

    public static LangBuilder lang() {
        return new LangBuilder(MOD_ID);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Pondering about what to do");
        PonderClient.init();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new net.createmod.ponder1710.foundation.PonderForgeEvents());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Ponder now knows what to do!");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Ponder is registering scenes");
        PonderClient.modLoadCompleted();
    }
}
