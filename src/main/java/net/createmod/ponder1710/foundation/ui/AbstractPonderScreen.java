package net.createmod.ponder1710.foundation.ui;

import static net.createmod.ponder1710.foundation.registration.PonderLocalization.UI_PREFIX;

// import net.createmod.catnip.gui.NavigatableSimiScreen; // TODO: catnip not available
// import net.createmod.catnip.gui.ScreenOpener; // TODO: catnip not available
// import net.minecraft.network.chat.Component; // not available in 1.7.10

import net.minecraft.client.gui.GuiScreen;

public abstract class AbstractPonderScreen extends GuiScreen {

    public static final String INDEX_TITLE = UI_PREFIX + "index_title";
    public static final String WELCOME = UI_PREFIX + "welcome";
    public static final String CATEGORIES = UI_PREFIX + "categories";
    public static final String DESCRIPTION = UI_PREFIX + "index_description";

    public static final String PONDERING = UI_PREFIX + "pondering";
    public static final String PONDERING_TAG = UI_PREFIX + "pondering_tag";
    public static final String IDENTIFY_MODE = UI_PREFIX + "identify_mode";
    public static final String IN_CHAPTER = UI_PREFIX + "in_chapter";
    public static final String IDENTIFY = UI_PREFIX + "identify";
    public static final String PREVIOUS = UI_PREFIX + "previous";
    public static final String CLOSE = UI_PREFIX + "close";
    public static final String NEXT = UI_PREFIX + "next";
    public static final String NEXT_UP = UI_PREFIX + "next_up";
    public static final String REPLAY = UI_PREFIX + "replay";
    public static final String SLOW_TEXT = UI_PREFIX + "slow_text";
    public static final String THINK_BACK = UI_PREFIX + "think_back";
    public static final String EXIT = UI_PREFIX + "exit";
    public static final String ASSOCIATED = UI_PREFIX + "associated";

    @Override
    public void initGui() {
        super.initGui();
        // TODO: backTrack button from NavigatableSimiScreen not available in 1.7.10
    }
}
