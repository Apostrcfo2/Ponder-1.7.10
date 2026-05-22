package net.createmod.metanip.levelWrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.createmod.metanip.math.BBHelper;
import net.createmod.ponder1710.Ponder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSettings;

// SchematicLevel extends WorldClient - a client-side virtual world for Ponder scenes.
// No physics, no networking, no SubWorldClient needed.
// Blocks are stored in a flat HashMap and rendered directly.
public class SchematicLevel extends WorldClient {

    // Block storage: encoded position -> {block, metadata}
    protected final Map<Long, Object[]> blocks = new HashMap<>();
    protected final Map<Long, TileEntity> tileEntities = new HashMap<>();
    protected final List<TileEntity> renderedTileEntities = new ArrayList<>();
    protected final List<Entity> entityList2 = new ArrayList<>();

    // Bounding box as int[] {minX,minY,minZ,maxX,maxY,maxZ}
    protected int[] bounds = {0, 0, 0, 0, 0, 0};

    // Anchor position int[] {x,y,z}
    public int[] anchor = {0, 0, 0};
    public boolean renderMode;

    public SchematicLevel() {
        this(new int[]{0, 0, 0});
    }

    public SchematicLevel(int[] anchor) {
        // Use the real world's net handler and settings
        super(
            Minecraft.getMinecraft().getNetHandler(),
            new WorldSettings(0L, Minecraft.getMinecraft().theWorld.getWorldInfo().getGameType(), false, false,
                Minecraft.getMinecraft().theWorld.getWorldInfo().getTerrainType()),
            0, // dimension - use overworld
            Minecraft.getMinecraft().theWorld.difficultySetting,
            Minecraft.getMinecraft().mcProfiler
        );
        this.anchor = anchor.clone();
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

    // Override WorldClient block access
    @Override
    public Block getBlock(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        if (ry - bounds[1] == -1 && !renderMode)
            return Blocks.dirt;
        long key = encodePos(rx, ry, rz);
        if (isInBounds(rx, ry, rz) && blocks.containsKey(key))
            return (Block) blocks.get(key)[0];
        return Blocks.air;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        long key = encodePos(rx, ry, rz);
        if (isInBounds(rx, ry, rz) && blocks.containsKey(key))
            return (int) blocks.get(key)[1];
        return 0;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        long key = encodePos(rx, ry, rz);
        if (tileEntities.containsKey(key))
            return tileEntities.get(key);
        if (!blocks.containsKey(key)) return null;

        Block block = getBlock(x, y, z);
        int meta = getBlockMetadata(x, y, z);
        if (block.hasTileEntity(meta)) {
            try {
                TileEntity te = block.createTileEntity(this, meta);
                if (te != null) {
                    te.xCoord = x; te.yCoord = y; te.zCoord = z;
                    te.setWorldObj(this);
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

    @Override
    public boolean setBlock(int x, int y, int z, Block block, int meta, int flags) {
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

    @Override
    public boolean func_147480_a(int x, int y, int z, boolean drop) {
        return setBlock(x, y, z, Blocks.air, 0, 3);
    }

    // Full bright - schematic world has no light engine
    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int min) {
        return 0xF000F0; // full bright
    }

    @Override
    public int getSkyBlockTypeBrightness(int p_72801_1_, int x, int y, int z) {
        return 15;
    }

    @Override
    public boolean addEntity(Entity entity) {
        return entityList2.add(entity);
    }

    public boolean isInBounds(int rx, int ry, int rz) {
        return rx >= bounds[0] && rx <= bounds[3]
            && ry >= bounds[1] && ry <= bounds[4]
            && rz >= bounds[2] && rz <= bounds[5];
    }

    // Restore blocks from backup NBT
    public void restore() {
        // TODO: implement backup/restore
    }

    public int[] getBounds() { return bounds; }
    public void setBounds(int[] bounds) { this.bounds = bounds; }

    public Iterable<TileEntity> getTileEntitiesIterable() {
        return tileEntities.values();
    }

    public Iterable<TileEntity> getRenderedTileEntities() {
        return renderedTileEntities;
    }

    @Override
    public List getLoadedEntityList() {
        return entityList2;
    }

    // No players in schematic world
    @Override
    public EntityPlayer getPlayerEntityByName(String name) { return null; }

    @Override
    public List getPlayers(Class c, com.google.common.base.Predicate p) {
        return Collections.emptyList();
    }
}
