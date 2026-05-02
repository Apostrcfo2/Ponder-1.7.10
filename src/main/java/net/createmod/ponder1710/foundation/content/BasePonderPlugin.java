package net.createmod.ponder1710.foundation.content;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.registration.PonderPlugin;
import net.createmod.ponder1710.api.registration.SharedTextRegistrationHelper;

public class BasePonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return Ponder.MOD_ID;
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        helper.registerSharedText("sneak_and", "Sneak +");
        helper.registerSharedText("ctrl_and", "Ctrl +");
    }
}
