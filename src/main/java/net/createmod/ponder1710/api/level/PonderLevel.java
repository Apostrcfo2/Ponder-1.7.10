package net.createmod.ponder1710.api.level;

import java.util.ArrayList;
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
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import su.sergiusonesimus.metaworlds.client.multiplayer.SubWorldClient;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;

// PonderLevel extends SubWorldClient (MetaWorld Mixins)
// Each Ponder scene is a real SubWorldClient registered in parent.getSubWorldsMap()
// canUpdate = false: Ponder controls ticking manually
// Coordinate transforms via IMixinWorld.transformToGlobal()
public class PonderLevel extends SubWorldClient {

    @Nullable public PonderScene scene;

    protected final Map<Long, Block>          originalBlocks;
    protected final Map<Long, Integer>        originalBlockMeta;
    protected final Map<Long, NBTTagCompound> originalBlockEntities;
    protected final Map<Long, Integer>        blockBreakingProgressions;
    protected final List<Entity>              originalEntities;
    protected final List<Entity>              entities;
    protected final PonderWorldParticles      particles;

    int overrideLight = -1;
    @Nullable Selection mask;
    boolean currentlyTickingEntities;

    // minX,minY,minZ,maxX,maxY,maxZ
    private int[] bounds = {0, 0, 0, 0, 0, 0};

    public PonderLevel(WorldClient parent, int subWorldId) {
        super(
            parent,
            subWorldId,
            Minecraft.getMinecraft().getNetHandler(),
            new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.DEFAULT),
            parent.provider.dimensionId,
            EnumDifficulty.PEACEFUL,
            new Profiler()
        );
        this.canUpdate = false;
        this.isRemote  = true;

        originalBlocks            = new HashMap<>();
        originalBlockMeta         = new HashMap<>();
        originalBlockEntities     = new HashMap<>();
        blockBreakingProgressions = new HashMap<>();
        originalEntities          = new ArrayList<>();
        entities                  = new ArrayList<>();
        particles                 = new PonderWorldParticles(this);
    }

    public void createBackup() {
        originalBlocks.clear();
        originalBlockMeta.clear();
        originalBlockEntities.clear();
        originalEntities.clear();

        for (int x = bounds[0]; x <= bounds[3]; x++) {
            for (int y = bounds[1]; y <= bounds[4]; y++) {
                for (int z = bounds[2]; z <= bounds[5]; z++) {
                    long key = posToLong(x, y, z);
                    originalBlocks.put(key, getBlock(x, y, z));
                    originalBlockMeta.put(key, getBlockMetadata(x, y, z));
                    TileEntity te = getTileEntity(x, y, z);
                    if (te != null) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        te.writeToNBT(nbt);
                        originalBlockEntities.put(key, nbt);
                    }
                }
            }
        }
    }

    public void restore() {
        entities.clear();
        blockBreakingProgressions.clear();
        particles.clearEffects();

        for (int x = bounds[0]; x <= bounds[3]; x++) {
            for (int y = bounds[1]; y <= bounds[4]; y++) {
                for (int z = bounds[2]; z <= bounds[5]; z++) {
                    long key  = posToLong(x, y, z);
                    Block b   = originalBlocks.getOrDefault(key, Blocks.air);
                    int meta  = originalBlockMeta.getOrDefault(key, 0);
                    setBlock(x, y, z, b, meta, 2);
                    if (originalBlockEntities.containsKey(key)) {
                        TileEntity te = getTileEntity(x, y, z);
                        if (te != null) te.readFromNBT(originalBlockEntities.get(key));
                    }
                }
            }
        }

        PonderIndex.forEachPlugin(plugin -> plugin.onPonderLevelRestore(this));
        redraw();
    }

    private void redraw() {
        if (scene != null)
            scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
    }

    @Override
    public void setBoundaries(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super.setBoundaries(minX, minY, minZ, maxX, maxY, maxZ);
        bounds[0] = minX; bounds[1] = minY; bounds[2] = minZ;
        bounds[3] = maxX; bounds[4] = maxY; bounds[5] = maxZ;
    }

    public int[] getBounds() { return bounds; }

    @Override
    public void tick() {
        // Ponder controls ticking — skip SubWorld physics
        currentlyTickingEntities = true;
        particles.tick();
        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
            Entity e = it.next();
            e.onUpdate();
            if (e.posY <= -.5f) e.setDead();
            if (e.isDead) it.remove();
        }
        currentlyTickingEntities = false;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minLight) {
        if (overrideLight != -1) return overrideLight;
        return 0xF000F0; // full bright
    }

    public void pushFakeLight(int light) { this.overrideLight = light; }
    public void popLight()               { this.overrideLight = -1; }
    public void setMask(@Nullable Selection mask) { this.mask = mask; }
    public void clearMask() { this.mask = null; }

    public void addBlockDestroyEffects(int x, int y, int z, Block block, int meta) {
        // TODO: EntityDiggingFX particles
    }

    public Map<Long, Integer> getBlockBreakingProgressions() { return blockBreakingProgressions; }
    public List<Entity> getEntityList() { return entities; }

    public static long posToLong(int x, int y, int z) {
        return ((long)(x & 0xFFFF)) | (((long)(y & 0xFFFF)) << 16) | (((long)(z & 0xFFFF)) << 32);
    }

    public static int[] decodePos(long key) {
        int x = (int)(key & 0xFFFF);
        int y = (int)((key >> 16) & 0xFFFF);
        int z = (int)((key >> 32) & 0xFFFF);
        return new int[]{x, y, z};
    }
}
