package net.createmod.metanip.platform;

// ServiceLoader not needed in 1.7.10 - direct Forge implementations
// ModFluidHelper, NetworkHelper etc. replaced with direct 1.7.10 equivalents

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.createmod.ponder1710.Ponder;

public class CatnipServices {

    // In 1.7.10 platform is always Forge
    public static final PlatformHelper PLATFORM = new PlatformHelper() {
        @Override
        public Env getEnv() {
            return FMLLaunchHandler.side().isClient() ? Env.CLIENT : Env.SERVER;
        }

        @Override
        public Loader getLoader() {
            return Loader.FORGE;
        }

        @Override
        public String getModDisplayName(String modId) {
            cpw.mods.fml.common.ModContainer mod = cpw.mods.fml.common.Loader.instance().getIndexedModList().get(modId);
            return mod != null ? mod.getName() : modId;
        }
    };
}
