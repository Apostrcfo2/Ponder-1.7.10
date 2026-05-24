package net.createmod.ponder1710.api.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.PoseStack; // TODO: not available in 1.7.10 - use GL11
// import net.createmod.metanip.levelWrappers.SchematicLevel; // TODO: catnip not available
// import net.createmod.metanip.levelWrappers.WrappedClientLevel; // TODO: catnip not available
// import net.createmod.metanip.platform.CatnipClientServices; // TODO: catnip not available
// import net.createmod.metanip.render.SuperRenderTypeBuffer; // TODO: catnip not available

import net.createmod.ponder1710.api.VirtualBlockEntity;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.PonderWorldParticles;

// 1.7.10 equivalents
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

// Commented - not available in 1.7.10:
// import net.minecraft.client.Camera; // no Camera in 1.7.10
// import net.minecraft.client.multiplayer.ClientLevel; // ClientLevel = WorldClient in 1.7.10
// import net.minecraft.client.particle.Particle; // different in 1.7.10
// import net.minecraft.client.renderer.MultiBufferSource; // not available
// import net.minecraft.client.renderer.RenderType; // not available
// import net.minecraft.client.renderer.entity.EntityRenderDispatcher; // different in 1.7.10
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z or ChunkCoordinates
// import net.minecraft.core.particles.BlockParticleOption; // not available
// import net.minecraft.core.particles.ParticleOptions; // not available
// import net.minecraft.core.particles.ParticleTypes; // not available
// import net.minecraft.world.level.BlockGetter; // not available
// import net.minecraft.world.level.Level; // Level = World in 1.7.10
// import net.minecraft.world.level.LightLayer; // not available
// import net.minecraft.world.level.block.Blocks; // different in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntity; // BlockEntity = TileEntity in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // not available in 1.7.10
// import net.minecraft.world.phys.AABB; // AxisAlignedBB in 1.7.10
// import net.minecraft.world.phys.shapes.VoxelShape; // not available in 1.7.10

// TODO: PonderLevel currently extends nothing - needs to extend SubWorldClient from Metaworlds
// In the original it extends SchematicLevel from catnip which is equivalent to SubWorldClient
// This will be implemented when Metaworlds integration is added
public class PonderLevel {

    @Nullable
    public PonderScene scene;

    // TODO: BlockPos -> using int x,y,z in 1.7.10
    // TODO: BlockState -> using Block + metadata in 1.7.10
    // TODO: CompoundTag -> NBTTagCompound in 1.7.10
    // TODO: BlockEntity -> TileEntity in 1.7.10
    protected Map<Long, Block> originalBlocks;
    protected Map<Long, Integer> originalBlockMeta;
    protected Map<Long, NBTTagCompound> originalBlockEntities;
    protected Map<Long, Integer> blockBreakingProgressions;
    protected List<Entity> originalEntities;
    protected List<Entity> entities;

    protected PonderWorldParticles particles;

    int overrideLight;
    @Nullable
    Selection mask;
    boolean currentlyTickingEntities;

    public PonderLevel(World original) {
        originalBlocks = new HashMap<>();
        originalBlockMeta = new HashMap<>();
        originalBlockEntities = new HashMap<>();
        blockBreakingProgressions = new HashMap<>();
        originalEntities = new ArrayList<>();
        entities = new ArrayList<>();
        particles = new PonderWorldParticles(this);
    }

    public void createBackup() {
        // TODO: implement backup using 1.7.10 block/TileEntity system
    }

    public void restore() {
        entities.clear();
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

    // TODO: renderEntities - needs GL11 port
    // public void renderEntities(PoseStack ms, SuperRenderTypeBuffer buffer, Camera ari, float pt) { ... }

    // TODO: renderParticles - needs GL11 port
    // public void renderParticles(PoseStack ms, MultiBufferSource buffer, Camera ari, float pt) { ... }

    public void tick() {
        currentlyTickingEntities = true;
        particles.tick();

        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            entity.onUpdate();

            // entity.getY() <= -.5f equivalent in 1.7.10
            if (entity.posY <= -.5f)
                entity.setDead();

            if (entity.isDead)
                iterator.remove();
        }

        currentlyTickingEntities = false;
    }

    // TODO: addParticle - ParticleOptions not available in 1.7.10
    // public void addParticle(ParticleOptions data, ...) { ... }

    public void addBlockDestroyEffects(int x, int y, int z, Block block, int meta) {
        // TODO: reimplement using 1.7.10 particle system
    }

    public Map<Long, Integer> getBlockBreakingProgressions() {
        return blockBreakingProgressions;
    }

    public List<Entity> getEntityList() {
        return entities;
    }

    // Helper to encode x,y,z into a long key
    public static long posToLong(int x, int y, int z) {
        return ((long) x & 0xFFFFFFFFL) | (((long) y & 0xFFFFFFFFL) << 32) | (((long) z & 0xFFFFFFFFL) << 48);
    }
}
