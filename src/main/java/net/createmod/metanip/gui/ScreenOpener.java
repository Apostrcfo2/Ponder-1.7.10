package net.createmod.metanip.gui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ScreenOpener {

    private static final Deque<GuiScreen> backStack = new ArrayDeque<>();
    @Nullable
    private static GuiScreen backSteppedFrom = null;

    public static void open(@Nullable GuiScreen screen) {
        open(Minecraft.getMinecraft().currentScreen, screen);
    }

    public static void open(@Nullable GuiScreen current, @Nullable GuiScreen toOpen) {
        backSteppedFrom = null;
        if (current != null) {
            if (backStack.size() >= 15)
                backStack.pollLast();
            backStack.push(current);
        } else {
            backStack.clear();
        }
        openScreen(toOpen);
    }

    public static void openPreviousScreen(GuiScreen current, @Nullable NavigatableSimiScreen screenWithContext) {
        if (backStack.isEmpty()) return;
        backSteppedFrom = current;
        GuiScreen previousScreen = backStack.pop();
        if (previousScreen instanceof NavigatableSimiScreen previousNavScreen) {
            if (screenWithContext != null)
                screenWithContext.shareContextWith(previousNavScreen);
            previousNavScreen.transition
                .startWithValue(-0.001)
                .chase(-1, .3f, LerpedFloat.Chaser.EXP);
        }
        openScreen(previousScreen);
    }

    public static void transitionTo(NavigatableSimiScreen screen) {
        if (tryBackTracking(screen)) return;
        screen.transition.startWithValue(0.001)
            .chase(1, .3f, LerpedFloat.Chaser.EXP);
        open(screen);
    }

    private static boolean tryBackTracking(NavigatableSimiScreen screen) {
        List<GuiScreen> history = getScreenHistory();
        if (history.isEmpty()) return false;
        GuiScreen previous = history.get(0);
        if (!(previous instanceof NavigatableSimiScreen)) return false;
        if (!screen.isEquivalentTo((NavigatableSimiScreen) previous)) return false;
        openPreviousScreen(Minecraft.getMinecraft().currentScreen, screen);
        return true;
    }

    public static void clearStack() { backStack.clear(); }

    public static List<GuiScreen> getScreenHistory() { return new ArrayList<>(backStack); }

    @Nullable
    public static GuiScreen getBackStepScreen() { return backStack.peek(); }

    @Nullable
    public static GuiScreen getPreviouslyRenderedScreen() {
        return backSteppedFrom != null ? backSteppedFrom : backStack.peek();
    }

    private static void openScreen(@Nullable GuiScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
}
