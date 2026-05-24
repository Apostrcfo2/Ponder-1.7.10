package net.createmod.ponder1710;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import net.createmod.metanip.lang.LangBuilder; // TODO: catnip not available in 1.7.10
// import net.createmod.metanip.net.CatnipPackets; // TODO: catnip not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different ResourceLocation

import net.minecraft.util.ResourceLocation;

public class Ponder {

    public static final String MOD_ID = "ponder1710";
    public static final String MOD_NAME = "Ponder 1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    // TODO: LangBuilder from catnip not available in 1.7.10
    // public static LangBuilder lang() {
    //     return new LangBuilder(MOD_ID);
    // }

    public static ResourceLocation asResource(String path) {
        // ResourceLocation.fromNamespaceAndPath not available in 1.7.10
        return new ResourceLocation(MOD_ID, path);
    }

    public static void init() {
        // CatnipPackets.register(); // TODO: catnip not available in 1.7.10
    }
}
