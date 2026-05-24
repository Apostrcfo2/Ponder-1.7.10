package net.createmod.ponder1710.command;

// import net.createmod.metanip.gui.ScreenOpener; // TODO: catnip not available
// import net.minecraft.resources.ResourceLocation; // 1.7.10 uses different package
import net.minecraft.util.ResourceLocation;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.ui.PonderIndexScreen;
import net.createmod.ponder1710.foundation.ui.PonderTagIndexScreen;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;

public class SimplePonderActions {

    public static void openPonder(String value) {
        if (value.equals("index") || value.equals("ponder:index")) {
            // TODO: ScreenOpener.transitionTo not available - use Minecraft.displayGuiScreen
            Minecraft.getMinecraft().displayGuiScreen(new PonderIndexScreen());
            return;
        }

        if (value.equals("ponder:tags")) {
            Minecraft.getMinecraft().displayGuiScreen(new PonderTagIndexScreen());
            return;
        }

        // ResourceLocation.parse not available in 1.7.10
        ResourceLocation id = new ResourceLocation(value);
        if (!PonderIndex.getSceneAccess().doScenesExistForId(id)) {
            Ponder.LOGGER.error("Could not find ponder scenes for item: " + id);
            return;
        }

        Minecraft.getMinecraft().displayGuiScreen(PonderUI.of(id));
    }

    public static void reloadPonder(String value) {
        PonderIndex.reload();
    }
}
