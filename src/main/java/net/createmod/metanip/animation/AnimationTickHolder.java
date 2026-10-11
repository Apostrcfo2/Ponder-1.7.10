package net.createmod.metanip.animation;

// import net.createmod.metanip.levelWrappers.WrappedClientLevel; // TODO: port later
// import net.minecraft.client.DeltaTracker; // not available in 1.7.10
// import net.minecraft.world.level.LevelAccessor; // not available in 1.7.10
// import net.createmod.ponder1710.mixin.accessor.TimerAccessor; // not available

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;

public class AnimationTickHolder {

    private static int ticks;
    private static int pausedTicks;

    public static void reset() {
        ticks = 0;
        pausedTicks = 0;
    }

    public static void tick() {
        if (!Minecraft.getMinecraft().isGamePaused()) {
            ticks = (ticks + 1) % 1_728_000;
        } else {
            pausedTicks = (pausedTicks + 1) % 1_728_000;
        }
    }

    public static int getTicks() {
        return getTicks(false);
    }

    public static int getTicks(boolean includePaused) {
        return includePaused ? ticks + pausedTicks : ticks;
    }

    public static int getTicks(Object level) {
        if (level instanceof PonderLevel)
            return PonderUI.ponderTicks;
        return getTicks();
    }

    public static float getPartialTicks(Object level) {
        if (level instanceof PonderLevel)
            return PonderUI.getPartialTicks();
        return getPartialTicks();
    }

    public static float getRenderTime() {
        return getTicks() + getPartialTicks();
    }

    public static float getRenderTime(Object level) {
        return getTicks(level) + getPartialTicks(level);
    }

    // Partial ticks frozen during pause
    public static float getPartialTicks() {
        try { java.lang.reflect.Field tf = Minecraft.class.getDeclaredField("timer"); tf.setAccessible(true); net.minecraft.util.Timer t = (net.minecraft.util.Timer)tf.get(Minecraft.getMinecraft()); return t.renderPartialTicks; } catch(Exception e) { return 0; }
    }

    // Partial ticks not frozen during pause
    public static float getPartialTicksUI() {
        try { java.lang.reflect.Field tf = Minecraft.class.getDeclaredField("timer"); tf.setAccessible(true); net.minecraft.util.Timer t = (net.minecraft.util.Timer)tf.get(Minecraft.getMinecraft()); return t.renderPartialTicks; } catch(Exception e) { return 0; }
    }
}
