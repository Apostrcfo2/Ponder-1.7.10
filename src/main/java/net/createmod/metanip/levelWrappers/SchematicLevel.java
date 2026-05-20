package net.createmod.metanip.levelWrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.createmod.metanip.math.BBHelper;
import net.createmod.ponder1710.Ponder;

// Modern imports replaced with 1.7.10 equivalents:
// BlockPos -> int[] {x,y,z}
// BlockState -> Block + metadata
// BlockEntity -> TileEntity
// Level -> World
// BoundingBox -> int[] {minX,minY,minZ,maxX,maxY,maxZ}
// ServerLevelAccessor -> not needed
// Entity -> net.minecraft.entity.Entity

// MetaWorld Mixins: SubWorldClient is our virtual world backend
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

// SubWorldClient from MetaWorld Mixins - our schematic world backend
// import gordonfromblocks.metaworldmixins.api.SubWorldClient;

public class SchematicLevel {

    // Block storage: encoded position -> {block, metadata}
    protected final Map<Long, Object[]> blocks = new HashMap<>();
    protected final Map<Long, TileEntity> tileEntities = new HashMap<>();
    protected final List<TileEntity> renderedTileEntities = new ArrayList<>();
    protected final List<Entity> entities = new ArrayList<>();

    // Bounding box as int[] {minX,minY,minZ,maxX,maxY,maxZ}
    protected int[] bounds = {0, 0, 0, 0, 0, 0};

    // Anchor position int[] {x,y,z}
    public int[] anchor;
    public boolean renderMode;

    // The real world for fallback
    protected final World realWorld;

    // TODO: integrate SubWorldClient from MetaWorld Mixins as backing world
    // protected SubWorldClient subWorld;

    public SchematicLevel(World realWorld) {
        this(new int[]{0, 0, 0}, realWorld);
    }

    public SchematicLevel(int[] anchor, World realWorld) {
        this.anchor = anchor.clone();
        this.realWorld = realWorld;
    }

    // Encode block position to long key
    public static long encodePos(int x, int y, int z) {
        return ((long)(x + 30000000)) | ((long)(y + 30000000) << 20) | ((long)(z + 30000000) << 40);
    }

    public static int[] decodePos(long key) {
        return new int[]{
            (int)((key & 0xFFFFF) - 30000000),
            (int)(((key >> 20) & 0xFFFFF) - 30000000),
            (int)(((key >> 40) & 0xFFFFF) - 30000000)
        };
    }

    public Set<Long> getAllPositionKeys() {
        return blocks.keySet();
    }

    public boolean addFreshEntity(Entity entity) {
        return entities.add(entity);
    }

    public List<Entity> getEntityList() {
        return entities;
    }

    public TileEntity getTileEntity(int x, int y, int z) {
        long key = encodePos(x - anchor[0], y - anchor[1], z - anchor[2]);
        if (tileEntities.containsKey(key))
            return tileEntities.get(key);
        if (!blocks.containsKey(key))
            return null;

        Block block = getBlock(x, y, z);
        if (block.hasTileEntity(getBlockMeta(x, y, z))) {
            try {
                TileEntity te = block.createTileEntity(realWorld, getBlockMeta(x, y, z));
                if (te != null) {
                    te.xCoord = x; te.yCoord = y; te.zCoord = z;
                    tileEntities.put(key, te);
                    renderedTileEntities.add(te);
                }
                return te;
            } catch (Exception e) {
                Ponder.LOGGER.debug("Could not create TileEntity of block " + block, e);
            }
        }
        return null;
    }

    public Block getBlock(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        if (ry - bounds[1] == -1 && !renderMode)
            return Blocks.dirt;
        long key = encodePos(rx, ry, rz);
        if (isInBounds(rx, ry, rz) && blocks.containsKey(key))
            return (Block) blocks.get(key)[0];
        return Blocks.air;
    }

    public int getBlockMeta(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        long key = encodePos(rx, ry, rz);
        if (isInBounds(rx, ry, rz) && blocks.containsKey(key))
            return (int) blocks.get(key)[1];
        return 0;
    }

    public boolean setBlock(int x, int y, int z, Block block, int meta) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        long key = encodePos(rx, ry, rz);
        bounds = BBHelper.encapsulate(bounds, rx, ry, rz);
        blocks.put(key, new Object[]{block, meta});

        // Remove incompatible TileEntity
        if (tileEntities.containsKey(key)) {
            TileEntity te = tileEntities.get(key);
            if (!block.hasTileEntity(meta)) {
                tileEntities.remove(key);
                renderedTileEntities.remove(te);
            }
        }
        return true;
    }

    public boolean destroyBlock(int x, int y, int z) {
        return setBlock(x, y, z, Blocks.air, 0);
    }

    public boolean isInBounds(int rx, int ry, int rz) {
        return rx >= bounds[0] && rx <= bounds[3]
            && ry >= bounds[1] && ry <= bounds[4]
            && rz >= bounds[2] && rz <= bounds[5];
    }

    public int[] getBounds() { return bounds; }
    public void setBounds(int[] bounds) { this.bounds = bounds; }

    public Iterable<TileEntity> getTileEntities() {
        return tileEntities.values();
    }

    public Iterable<TileEntity> getRenderedTileEntities() {
        return renderedTileEntities;
    }

    public int getBrightness(int x, int y, int z) {
        return 15; // full bright for schematic world
    }

    public List<? extends EntityPlayer> getPlayers() {
        return Collections.emptyList();
    }

    public World getRealWorld() {
        return realWorld;
    }
}
