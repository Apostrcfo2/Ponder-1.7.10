package net.createmod.ponder1710.api.level;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderWorldParticles;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.client.multiplayer.SubWorldClient;

// PonderLevel extends SubWorldClient - same pattern as ContraptionWorldClient in ReCreate
// This gives us full block rendering, TileEntity animation, physics, lighting etc.
@SideOnly(Side.CLIENT)
public class PonderLevel extends SubWorldClient {

    @Nullable
    public PonderScene scene;

    protected Map<Long, Integer> blockBreakingProgressions = new HashMap<>();
    protected PonderWorldParticles particles;

    int overrideLight = -1;
    @Nullable
    Selection mask;
    boolean currentlyTickingEntities;

    public PonderLevel(WorldClient parentWorld, int subWorldID) {
        super(
            parentWorld,
            subWorldID,
            Minecraft.getMinecraft().getNetHandler(),
            new WorldSettings(
                0L,
                parentWorld.getWorldInfo().getGameType(),
                false,
                false,
                parentWorld.getWorldInfo().getTerrainType()
            ),
            0, // dimension - overworld
            parentWorld.difficultySetting,
            parentWorld.theProfiler
        );
        this.particles = new PonderWorldParticles(this);
    }

    public void pushFakeLight(int light) {
        this.overrideLight = light;
    }

    public void popLight() {
        this.overrideLight = -1;
    }

    public void setMask(@Nullable Selection mask) {
        this.mask = mask;
    }

    public void clearMask() {
        this.mask = null;
    }

    // Full bright for Ponder scenes, unless overridden
    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int min) {
        if (overrideLight != -1)
            return overrideLight << 20 | overrideLight << 4;
        return 0xF000F0;
    }

    public void renderEntities(float pt) {
        for (Entity entity : (List<Entity>) getLoadedEntityList()) {
            net.minecraft.client.renderer.entity.RenderManager.instance.renderEntityWithPosYaw(
                entity,
                entity.prevPosX + (entity.posX - entity.prevPosX) * pt,
                entity.prevPosY + (entity.posY - entity.prevPosY) * pt,
                entity.prevPosZ + (entity.posZ - entity.prevPosZ) * pt,
                entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * pt,
                pt
            );
        }
    }

    public void renderParticles(float pt) {
        particles.renderParticles(pt);
    }

    public void tick() {
        super.tick();
        currentlyTickingEntities = true;
        particles.tick();

        for (Iterator<Entity> it = ((List<Entity>) getLoadedEntityList()).iterator(); it.hasNext();) {
            Entity entity = it.next();
            entity.onUpdate();
            if (entity.posY <= -.5f) entity.setDead();
            if (entity.isDead) it.remove();
        }

        currentlyTickingEntities = false;
    }

    public void addBlockDestroyEffects(int x, int y, int z, Block block, int meta) {
        Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x, y, z, block, meta);
    }

    public void restoreBlocks(Selection selection) {
        // TODO: implement block restore from backup using selection
    }

    public Map<Long, Integer> getBlockBreakingProgressions() {
        return blockBreakingProgressions;
    }

    // Helper to encode x,y,z into long key
    public static long posToLong(int x, int y, int z) {
        return ((long)(x + 30000000)) | ((long)(y + 30000000) << 20) | ((long)(z + 30000000) << 40);
    }
}
