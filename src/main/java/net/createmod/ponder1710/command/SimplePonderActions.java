package net.createmod.ponder1710.command;

import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.ui.PonderIndexScreen;
import net.createmod.ponder1710.foundation.ui.PonderTagIndexScreen;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class SimplePonderActions {

    public static void openPonder(String value) {
        if (value.equals("index") || value.equals("ponder:index")) {
            ScreenOpener.transitionTo(new PonderIndexScreen());
            return;
        }

        if (value.equals("ponder:tags")) {
            ScreenOpener.transitionTo(new PonderTagIndexScreen());
            return;
        }

        ResourceLocation id = new ResourceLocation(value);
        if (!PonderIndex.getSceneAccess().doScenesExistForId(id)) {
            Ponder.LOGGER.error("Could not find ponder scenes for item: " + id);
            return;
        }

        ScreenOpener.transitionTo(PonderUI.of(id));
    }

    public static void reloadPonder(String value) {
        PonderIndex.reload();
    }

    // Called server-side from PonderCommand to open UI on client
    public static void openPonderForId(EntityPlayerMP player, ResourceLocation id) {
        // In 1.7.10 use FML network to send packet to client
        // For now open directly if on client
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.thePlayer != null)
            ScreenOpener.transitionTo(PonderUI.of(id));
    }
}
