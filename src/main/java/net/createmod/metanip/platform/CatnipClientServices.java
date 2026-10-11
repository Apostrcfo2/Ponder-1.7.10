package net.createmod.metanip.platform;

import java.util.Locale;

import net.minecraft.client.Minecraft;

public class CatnipClientServices extends CatnipServices {

    public static final ClientHooks CLIENT_HOOKS = new ClientHooks();

    public static class ClientHooks {
        public Locale getCurrentLocale() {
            // In 1.7.10 locale comes from game settings
            String lang = Minecraft.getMinecraft().getLanguageManager()
                .getCurrentLanguage().getLanguageCode();
            return Locale.forLanguageTag(lang.replace("_", "-"));
        }
    }
}
