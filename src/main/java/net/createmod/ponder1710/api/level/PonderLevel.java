package net.createmod.ponder1710.api.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.createmod.metanip.levelWrappers.SchematicLevel;
import net.createmod.ponder1710.api.VirtualBlockEntity;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderWorldParticles;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

// PonderLevel extends SchematicLevel - a WorldClient-based virtual world for Ponder scenes
public class PonderLevel extends SchematicLevel {

    @Nullable
    public PonderScene scene;

    protected Map<Long, Integer> blockBreakingProgressions = new HashMap<>();
    protected List<Entity> entities2 = new ArrayList<>(); // separate from WorldClient's entityList

    protected PonderWorldParticles particles;

    int overrideLight = -1;
    @Nullable
    Selection mask;
    boolean currentlyTickingEntities;

    public PonderLevel(World original) {
        super(original);
        particles = new PonderWorldParticles(this);
    }

    public PonderLevel(int[] anchor, World original) {
        super(anchor, original);
        particles = new PonderWorldParticles(this);
    }

    @Override
    public void createBackup() {
        super.createBackup();
        // Also backup entities
    }

    @Override
    public void restore() {
        super.restore();
        entities2.clear();
        blockBreakingProgressions.clear();
        particles.clearEffects();
        PonderIndex.forEachPlugin(plugin -> plugin.onPonderLevelRestore(this));
    }

    private void redraw() {
        if (scene != null)
            scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
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

    // Render entities using 1.7.10 RenderManager
    public void renderEntities(float pt) {
        for (Entity entity : entities2) {
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

    // Render particles using 1.7.10 system
    public void renderParticles(float pt) {
        particles.renderParticles(pt);
    }

    public void tick() {
        currentlyTickingEntities = true;
        particles.tick();

        for (Iterator<Entity> iterator = entities2.iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            entity.onUpdate();
            if (entity.posY <= -.5f) entity.setDead();
            if (entity.isDead) iterator.remove();
        }

        currentlyTickingEntities = false;
    }

    public void addBlockDestroyEffects(int x, int y, int z, Block block, int meta) {
        // Use 1.7.10 effect renderer
        Minecraft mc = Minecraft.getMinecraft();
        mc.effectRenderer.addBlockDestroyEffects(x, y, z, block, meta);
    }

    // Override getLightBrightnessForSkyBlocks to support fake light
    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int min) {
        if (overrideLight != -1)
            return overrideLight << 20 | overrideLight << 4;
        return 0xF000F0; // full bright by default for Ponder
    }

    @Override
    public boolean addEntity(Entity entity) {
        return entities2.add(entity);
    }

    @Override
    public List getLoadedEntityList() {
        return entities2;
    }

    public Map<Long, Integer> getBlockBreakingProgressions() {
        return blockBreakingProgressions;
    }

    public List<Entity> getEntityList() {
        return entities2;
    }

    // Helper to encode x,y,z into a long key
    public static long posToLong(int x, int y, int z) {
        return ((long)(x + 30000000)) | ((long)(y + 30000000) << 20) | ((long)(z + 30000000) << 40);
    }
}
