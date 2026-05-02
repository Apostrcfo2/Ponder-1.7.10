package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Strings;

// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import net.createmod.catnip.animation.AnimationTickHolder; // TODO: catnip not available
// import net.createmod.catnip.animation.LerpedFloat; // TODO: catnip not available
// import net.createmod.catnip.data.Couple; // TODO: catnip not available
// import net.createmod.catnip.gui.NavigatableSimiScreen; // TODO: catnip not available
// import net.createmod.catnip.gui.ScreenOpener; // TODO: catnip not available
// import net.createmod.catnip.registry.RegisteredObjectsHelper; // TODO: catnip not available
// import net.createmod.catnip.theme.Color; // TODO: catnip not available
// import net.minecraft.ChatFormatting; // different in 1.7.10
// import net.minecraft.client.gui.Font; // FontRenderer in 1.7.10
// import net.minecraft.client.gui.screens.Screen; // GuiScreen in 1.7.10
// import net.minecraft.network.chat.Component; // not available in 1.7.10
// import net.minecraft.network.chat.MutableComponent; // not available in 1.7.10
// import net.minecraft.world.item.ItemStack; // different package in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.enums.PonderKeybinds;
import net.createmod.ponder1710.foundation.registration.PonderLocalization;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class PonderTooltipHandler {

    public static boolean enable = true;

    // TODO: LerpedFloat from catnip not available - replaced with simple float
    static float holdKeyProgress = 0;
    static ItemStack hoveredStack = null;
    static ItemStack trackingStack = null;
    static boolean subject = false;
    static boolean deferTick = false;

    static final List<Consumer<ItemStack>> hoveredStackCallbacks = new ArrayList<>();

    public static final String HOLD_TO_PONDER = PonderLocalization.UI_PREFIX + "hold_to_ponder";
    public static final String SUBJECT = PonderLocalization.UI_PREFIX + "subject";

    public static void tick() {
        deferTick = true;
    }

    public static void deferredTick() {
        deferTick = false;
        Minecraft mc = Minecraft.getMinecraft();

        if (hoveredStack == null || trackingStack == null) {
            trackingStack = null;
            holdKeyProgress = 0;
            return;
        }

        if (!subject && PonderKeybinds.PONDER.isDown() && mc.currentScreen != null) {
            if (holdKeyProgress >= 1) {
                mc.displayGuiScreen(PonderUI.of(trackingStack));
                holdKeyProgress = 0;
                return;
            }
            holdKeyProgress = Math.min(1, holdKeyProgress + Math.max(.25f, holdKeyProgress) * .25f);
        } else {
            holdKeyProgress = Math.max(0, holdKeyProgress - .05f);
        }

        hoveredStack = null;
    }

    // TODO: addToTooltip - tooltip system different in 1.7.10
    // In 1.7.10, tooltips are added via getItemStackDisplayName and getItemInformation

    public synchronized static void registerHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.add(consumer);
    }

    public synchronized static void removeHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.remove(consumer);
    }
}
