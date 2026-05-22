package net.createmod.metanip.gui;

import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.data.Couple;
import net.createmod.metanip.theme.Color;
import net.createmod.metanip.gui.widget.BoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class NavigatableSimiScreen extends AbstractSimiScreen {

    public static final Couple<Color> COLOR_NAV_ARROW = Couple.create(
        new Color(0x80_aa9999, true),
        new Color(0x30_aa9999)
    ).map(Color::setImmutable);

    protected static boolean currentlyRenderingPreviousScreen = false;

    protected int depthPointX, depthPointY;
    public final LerpedFloat transition = LerpedFloat.linear()
        .startWithValue(0)
        .chase(0, .1f, LerpedFloat.Chaser.LINEAR);
    protected final LerpedFloat arrowAnimation = LerpedFloat.linear()
        .startWithValue(0)
        .chase(0, 0.075f, LerpedFloat.Chaser.LINEAR);
    @Nullable
    protected BoxWidget backTrack;

    public NavigatableSimiScreen() {
        depthPointX = Minecraft.getMinecraft().displayWidth / 2;
        depthPointY = Minecraft.getMinecraft().displayHeight / 2;
    }

    @Override
    public void onGuiClosed() {
        ScreenOpener.clearStack();
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        transition.tickChaser();
        arrowAnimation.tickChaser();
    }

    @Override
    public void initGui() {
        super.initGui();
        backTrack = null;
        List<GuiScreen> history = ScreenOpener.getScreenHistory();
        if (history.isEmpty()) return;

        backTrack = new BoxWidget(31, height - 31 - 20, 20, 20);
        buttonList.add(backTrack);

        GuiScreen previousScreen = history.get(0);
        if (previousScreen instanceof NavigatableSimiScreen screen)
            screen.initBackTrackIcon(backTrack);
    }

    protected abstract void initBackTrackIcon(BoxWidget backTrack);

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_BACK) {
            ScreenOpener.openPreviousScreen(this, null);
            return;
        }
        try { super.keyTyped(typedChar, keyCode); } catch (Exception e) {}
    }

    @Override
    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        if (backTrack == null) return;
        if (backTrack == null) return;
        int x = (int)(arrowAnimation.getValue(0) * 30);
        UIRenderHelper.breadcrumbArrow(x, height - 51, 0, 30, 20, 5, NavigatableSimiScreen.COLOR_NAV_ARROW);
        UIRenderHelper.breadcrumbArrow(x - 30, height - 51, 0, 30, 20, 5, NavigatableSimiScreen.COLOR_NAV_ARROW);
    }

    public void centerScalingOn(int x, int y) {
        depthPointX = x;
        depthPointY = y;
    }

    public boolean isEquivalentTo(NavigatableSimiScreen other) { return false; }

    public void shareContextWith(NavigatableSimiScreen other) {}

    protected String getBreadcrumbTitle() {
        return this.getClass().getSimpleName();
    }

    public static boolean isCurrentlyRenderingPreviousScreen() {
        return currentlyRenderingPreviousScreen;
    }
}
