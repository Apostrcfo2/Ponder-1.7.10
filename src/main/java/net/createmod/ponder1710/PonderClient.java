package net.createmod.ponder1710;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.gui.UIRenderHelper;
import net.createmod.metanip.outliner.Outliner;

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.content.BasePonderPlugin;
import net.createmod.ponder1710.foundation.content.DebugPonderPlugin;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.Loader;

public class PonderClient {

    public static void init() {
        UIRenderHelper.init();
        PonderIndex.addPlugin(new BasePonderPlugin());

        // Register debug plugin in dev environment
        if (Loader.instance().isInDevelopmentEnvironment()) {
            PonderIndex.addPlugin(new DebugPonderPlugin());
        }
    }

    public static void modLoadCompleted() {
        PonderIndex.registerAll();
    }

    public static void onTick() {
        AnimationTickHolder.tick();
        if (!isGameActive()) return;
        Outliner.getInstance().tickOutlines();
    }

    public static void onRenderWorld() {
        if (!isGameActive()) return;
        float pt = AnimationTickHolder.getPartialTicks();
        Outliner.getInstance().renderOutlines(
            new org.joml.Matrix4f(),
            net.minecraft.util.Vec3.createVectorHelper(0, 0, 0),
            pt
        );
    }

    public static void invalidateRenderers() {}

    public static boolean isGameActive() {
        return Minecraft.getMinecraft().theWorld != null
            && Minecraft.getMinecraft().thePlayer != null;
    }
}
