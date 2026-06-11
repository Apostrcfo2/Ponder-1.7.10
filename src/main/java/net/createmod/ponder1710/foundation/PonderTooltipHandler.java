package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Strings;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.gui.ScreenOpener;
import net.createmod.metanip.registry.RegisteredObjectsHelper;
import net.createmod.ponder1710.enums.PonderKeybinds;
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
    public static final String SUBJECT        = PonderLocalization.UI_PREFIX + "subject";

    public static void tick() {
        deferTick = true;
    }

    public static void deferredTick() {
        deferTick = false;
        Minecraft mc = Minecraft.getMinecraft();

        if (hoveredStack == null || trackingStack == null) {
            trackingStack = null;
            holdKeyProgress.startWithValue(0);
            return;
        }

        float value = holdKeyProgress.getValue();

        if (!subject && PonderKeybinds.PONDER.isKeyDown() && mc.currentScreen != null) {
            if (value >= 1) {
                ScreenOpener.transitionTo(PonderUI.of(trackingStack));
                holdKeyProgress.startWithValue(0);
                return;
            }
            holdKeyProgress.setValue(Math.min(1, value + Math.max(.25f, value) * .25f));
        } else {
            holdKeyProgress.setValue(Math.max(0, value - .05f));
        }

        hoveredStack = null;
    }

    // Called from GuiContainerMixin / tooltip hook
    public static void addToTooltip(List<String> tooltip, ItemStack stack) {
        if (!enable) return;

        updateHovered(stack);

        if (deferTick) deferredTick();

        if (trackingStack != stack) return;

        float renderPT = AnimationTickHolder.getPartialTicksUI();
        float progress = Math.min(1, holdKeyProgress.getValue(renderPT) * 8 / 7f);

        String line;
        if (subject) {
            line = EnumChatFormatting.GREEN + "Pondering...";
        } else {
            line = makeProgressBar(progress);
        }

        if (tooltip.size() < 2) tooltip.add(line);
        else tooltip.add(1, line);
    }

    protected static void updateHovered(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean inPonderUI = mc.currentScreen instanceof PonderUI;

        ItemStack prevStack = trackingStack;
        hoveredStack = null;
        subject = false;

        if (inPonderUI) {
            PonderUI ponderUI = (PonderUI) mc.currentScreen;
            ItemStack uiSubject = ponderUI.getSubject();
            if (uiSubject != null && uiSubject.getItem() == stack.getItem())
                subject = true;
        }

        if (stack == null || stack.getItem() == null) return;

        ResourceLocation key = RegisteredObjectsHelper.getKeyOrThrow(stack.getItem());
        if (!PonderIndex.getSceneAccess().doScenesExistForId(key)) return;

        if (prevStack == null || prevStack.getItem() != stack.getItem())
            holdKeyProgress.startWithValue(0);

        hoveredStack = stack;
        trackingStack = stack;

        for (Consumer<ItemStack> cb : hoveredStackCallbacks)
            cb.accept(hoveredStack);
    }

    private static String makeProgressBar(float progress) {
        String keyName = PonderKeybinds.PONDER.getKeyDescription();

        if (progress > 0) {
            int total = 20;
            int current = (int)(progress * total);
            return EnumChatFormatting.GRAY + Strings.repeat("|", current)
                + EnumChatFormatting.DARK_GRAY + Strings.repeat("|", total - current);
        }

        return EnumChatFormatting.DARK_GRAY + "Hold "
            + EnumChatFormatting.GRAY + "[" + keyName + "]"
            + EnumChatFormatting.DARK_GRAY + " to Ponder";
    }

    public synchronized static void registerHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.add(consumer);
    }

    public synchronized static void removeHoveredPonderStackCallback(Consumer<ItemStack> consumer) {
        hoveredStackCallbacks.remove(consumer);
    }
}
