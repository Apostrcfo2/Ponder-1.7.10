package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Strings;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.registry.RegisteredObjectsHelper;
import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.enums.PonderKeybinds;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.registration.PonderLocalization;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class PonderTooltipHandler {

    public static boolean enable = true;

    static LerpedFloat holdKeyProgress = LerpedFloat.linear().startWithValue(0);
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
            holdKeyProgress.setValue(0);
            return;
        }

        if (!subject && PonderKeybinds.PONDER.isDown() && mc.currentScreen != null) {
            float progress = holdKeyProgress.getValue();
            if (progress >= 1) {
                mc.displayGuiScreen(PonderUI.of(trackingStack));
                holdKeyProgress.setValue(0);
                return;
            }
            holdKeyProgress.setValue(Math.min(1, progress + Math.max(.25f, progress) * .25f));
        } else {
            float progress = holdKeyProgress.getValue();
            holdKeyProgress.setValue(Math.max(0, progress - .05f));
        }

        hoveredStack = null;
    }

    // Called from GuiContainerMixin when an item is hovered
    public static void onHoveredItem(ItemStack stack) {
        if (!enable) return;
        if (stack == null || stack.getItem() == null) {
            hoveredStack = null;
            return;
        }

        hoveredStack = stack;

        // Check if item has ponder scenes
        ResourceLocation key = RegisteredObjectsHelper.getKeyOrThrow(stack.getItem());
        if (!PonderIndex.getSceneAccess().doScenesExistForId(key)) {
            hoveredStack = null;
            return;
        }

        trackingStack = stack;
        hoveredStackCallbacks.forEach(cb -> cb.accept(stack));
    }

    // Called from ItemStack.getTooltip hook in 1.7.10
    // Returns extra tooltip lines for the hovered item
    public static List<String> addToTooltip(ItemStack stack) {
        List<String> tooltip = new ArrayList<>();
        if (!enable || stack == null) return tooltip;

        ResourceLocation key = RegisteredObjectsHelper.getKeyOrThrow(stack.getItem());
        if (!PonderIndex.getSceneAccess().doScenesExistForId(key)) return tooltip;

        subject = false;
        float progress = holdKeyProgress.getValue();

        if (progress > 0) {
            // Show progress bar
            int filled = (int)(progress * 20);
            String bar = EnumChatFormatting.GREEN
                + Strings.repeat("|", filled)
                + EnumChatFormatting.DARK_GRAY
                + Strings.repeat("|", 20 - filled);
            tooltip.add(bar);
        } else {
            // Show hold hint
            String key1 = PonderKeybinds.PONDER.getKeyDescription();
            tooltip.add(EnumChatFormatting.GRAY + "Hold "
                + EnumChatFormatting.AQUA + key1
                + EnumChatFormatting.GRAY + " to Ponder");
        }

        return tooltip;
    }

    public synchronized static void registerHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.add(consumer);
    }

    public synchronized static void removeHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.remove(consumer);
    }
}
