package net.createmod.metanip.levelWrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.createmod.metanip.math.BBHelper;
import net.createmod.ponder1710.Ponder;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.client.multiplayer.WorldClient;

// 1.7.10 port of SchematicLevel (catnip) using WorldClient as base
// No SubWorldClient needed - this is a lightweight virtual world for block data
// Used for contraption rendering and similar in metanip
// PonderLevel uses SubWorldClient instead (MetaWorld integration)
public class SchematicLevel extends WorldClient implements SchematicLevelAccessor {

    // Block storage: encoded position -> {Block, int meta}
    protected final Map<Long, Object[]> blocks = new HashMap<>();
    protected final Map<Long, TileEntity> tileEntities = new HashMap<>();
    protected final List<TileEntity> renderedTileEntities = new ArrayList<>();
    protected final List<Entity> entityList2 = new ArrayList<>();

    // Bounds: {minX,minY,minZ,maxX,maxY,maxZ}
    protected int[] bounds = {0, 0, 0, 0, 0, 0};

    public int[] anchor = {0, 0, 0};
    public boolean renderMode;

    // Backup for restore()
    private Map<Long, Object[]> blocksBackup = null;
    private Map<Long, NBTTagCompound> tileEntityBackup = null;

    public SchematicLevel() {
        this(new int[]{0, 0, 0});
    }

    public SchematicLevel(int[] anchor) {
        super(
            Minecraft.getMinecraft().getNetHandler(),
            new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.DEFAULT),
            0,
            EnumDifficulty.PEACEFUL,
            new Profiler()
        );
        this.chunkProvider = new SchematicChunkSource(this);
        this.anchor = anchor.clone();
    }

    public static long encodePos(int x, int y, int z) {
        return ((long)(x + 30000000))
            | ((long)(y + 30000000) << 20)
            | ((long)(z + 30000000) << 40);
    }

    public static int[] decodePos(long key) {
        return new int[]{
            (int)((key & 0xFFFFF) - 30000000),
            (int)(((key >> 20) & 0xFFFFF) - 30000000),
            (int)(((key >> 40) & 0xFFFFF) - 30000000)
        };
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int rx = x - anchor[0], ry = y - anchor[1], rz = z - anchor[2];
        if (ry - bounds[1] == -1 && !renderMode) return Blocks.dirt;
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
        if (tileEntities.containsKey(key)) return tileEntities.get(key);
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
                Ponder.LOGGER.debug("Could not create TileEntity of block {}", block, e);
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

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int min) {
        return 0xF000F0; // full bright
    }

    @Override
    public int getSkyBlockTypeBrightness(int p1, int x, int y, int z) {
        return 15;
    }

    public void createBackup() {
        blocksBackup = new HashMap<>(blocks);
        tileEntityBackup = new HashMap<>();
        for (Map.Entry<Long, TileEntity> entry : tileEntities.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            entry.getValue().writeToNBT(nbt);
            tileEntityBackup.put(entry.getKey(), nbt);
        }
    }

    public void restore() {
        if (blocksBackup == null) return;
        blocks.clear();
        blocks.putAll(blocksBackup);
        tileEntities.clear();
        renderedTileEntities.clear();
        if (tileEntityBackup != null) {
            for (Map.Entry<Long, NBTTagCompound> entry : tileEntityBackup.entrySet()) {
                int[] pos = decodePos(entry.getKey());
                int ax = pos[0] + anchor[0], ay = pos[1] + anchor[1], az = pos[2] + anchor[2];
                Block block = getBlock(ax, ay, az);
                int meta = getBlockMetadata(ax, ay, az);
                if (block.hasTileEntity(meta)) {
                    try {
                        TileEntity te = block.createTileEntity(this, meta);
                        if (te != null) {
                            te.readFromNBT(entry.getValue());
                            te.xCoord = ax; te.yCoord = ay; te.zCoord = az;
                            te.setWorldObj(this);
                            tileEntities.put(entry.getKey(), te);
                            renderedTileEntities.add(te);
                        }
                    } catch (Exception e) {
                        Ponder.LOGGER.debug("Could not restore TileEntity", e);
                    }
                }
            }
        }
    }

    public boolean isInBounds(int rx, int ry, int rz) {
        return rx >= bounds[0] && rx <= bounds[3]
            && ry >= bounds[1] && ry <= bounds[4]
            && rz >= bounds[2] && rz <= bounds[5];
    }

    // SchematicLevelAccessor
    @Override public Set<Long> getAllPositions()               { return blocks.keySet(); }
    @Override public List<Entity> getEntityList()             { return entityList2; }
    @Override public Map<Long, Object[]> getBlockMap()        { return blocks; }
    @Override public int[] getBounds()                        { return bounds; }
    @Override public void setBounds(int[] bounds)             { this.bounds = bounds; }
    @Override public Iterable<TileEntity> getTileEntitiesIterable()  { return tileEntities.values(); }
    @Override public Iterable<TileEntity> getRenderedTileEntities()  { return renderedTileEntities; }

    @Override public List getLoadedEntityList()               { return entityList2; }
    @Override public EntityPlayer getPlayerEntityByName(String name) { return null; }
    @Override public List getPlayers(Class c, com.google.common.base.Predicate p) { return Collections.emptyList(); }
}
