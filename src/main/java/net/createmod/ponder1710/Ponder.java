package net.createmod.ponder1710;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.createmod.metanip.lang.LangBuilder;
import net.minecraft.util.ResourceLocation;

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

    public static void init() {
        // Network packets registered via FML in 1.7.10
    }
}
