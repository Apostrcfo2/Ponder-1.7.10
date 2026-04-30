package net.createmod.ponder1710;

// import com.mojang.blaze3d.vertex.PoseStack; // TODO: not available in 1.7.10 - use GL11
// import net.createmod.catnip.animation.AnimationTickHolder; // TODO: catnip not available
// import net.createmod.catnip.event.ClientResourceReloadListener; // TODO: catnip not available
// import net.createmod.catnip.ghostblock.GhostBlocks; // TODO: catnip not available
// import net.createmod.catnip.gui.UIRenderHelper; // TODO: catnip not available
// import net.createmod.catnip.net.packets.ClientboundSimpleActionPacket; // TODO: catnip not available
// import net.createmod.catnip.outliner.Outliner; // TODO: catnip not available
// import net.createmod.catnip.placement.PlacementClient; // TODO: catnip not available
// import net.createmod.catnip.platform.CatnipServices; // TODO: catnip not available
// import net.createmod.catnip.render.CachedBuffers; // TODO: catnip not available
// import net.createmod.catnip.render.DefaultSuperRenderTypeBuffer; // TODO: catnip not available
// import net.createmod.catnip.render.SuperByteBufferCache; // TODO: catnip not available
// import net.createmod.catnip.render.SuperRenderTypeBuffer; // TODO: catnip not available

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.content.BasePonderPlugin;
import net.createmod.ponder1710.foundation.content.DebugPonderPlugin;
import net.createmod.ponder1710.foundation.element.WorldSectionElementImpl;
import net.minecraft.client.Minecraft;

// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;

public class PonderClient {

    // TODO: ClientResourceReloadListener from catnip not available in 1.7.10
    // public static final ClientResourceReloadListener RESOURCE_RELOAD_LISTENER = new ClientResourceReloadListener();

    // TODO: GhostBlocks from catnip not available in 1.7.10
    // public static final GhostBlocks GHOST_BLOCKS = GhostBlocks.getInstance();

    public static void init() {
        // TODO: SuperByteBufferCache from catnip not available in 1.7.10
        // SuperByteBufferCache.getInstance().registerCompartment(CachedBuffers.GENERIC_BLOCK);
        // SuperByteBufferCache.getInstance().registerCompartment(WorldSectionElementImpl.PONDER_WORLD_SECTION);

        // TODO: UIRenderHelper from catnip not available in 1.7.10
        // UIRenderHelper.init();

        // TODO: ClientboundSimpleActionPacket from catnip not available in 1.7.10
        // ClientboundSimpleActionPacket.addAction("openPonder", () -> SimplePonderActions::openPonder);
        // ClientboundSimpleActionPacket.addAction("reloadPonder", () -> SimplePonderActions::reloadPonder);

        PonderIndex.addPlugin(new BasePonderPlugin());

        // TODO: CatnipServices not available - check dev environment differently
        // if (CatnipServices.PLATFORM.isDevelopmentEnvironment()) {
        //     PonderIndex.addPlugin(new DebugPonderPlugin());
        // }
    }

    public static void modLoadCompleted() {
        PonderIndex.registerAll();
    }

    public static void onTick() {
        // TODO: AnimationTickHolder from catnip not available in 1.7.10
        // AnimationTickHolder.tick();

        if (!isGameActive())
            return;

        // TODO: PlacementClient from catnip not available in 1.7.10
        // PlacementClient.tick();

        // TODO: GhostBlocks from catnip not available in 1.7.10
        // GhostBlocks.getInstance().tickGhosts();

        // TODO: Outliner from catnip not available in 1.7.10
        // Outliner.getInstance().tickOutlines();
    }

    // TODO: PoseStack not available in 1.7.10 - will be replaced with GL11
    // public static void onRenderWorld(PoseStack ms) { ... }
    public static void onRenderWorld() {
        // TODO: Reimplement using GL11 once catnip rendering is ported
        // Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        // float partialTicks = AnimationTickHolder.getPartialTicks();
        // GHOST_BLOCKS.renderAll(ms, buffer, cameraPos);
        // Outliner.getInstance().renderOutlines(ms, buffer, cameraPos, partialTicks);
    }

    public static void invalidateRenderers() {
        // TODO: SuperByteBufferCache from catnip not available in 1.7.10
        // SuperByteBufferCache.getInstance().invalidate();
    }

    public static boolean isGameActive() {
        return Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null;
    }
}
