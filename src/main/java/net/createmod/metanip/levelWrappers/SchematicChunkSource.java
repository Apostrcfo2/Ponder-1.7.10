package net.createmod.metanip.levelWrappers;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.EmptyChunk;

import java.util.List;

// 1.7.10 port of SchematicChunkSource
// ChunkSource -> IChunkProvider in 1.7.10
// Returns empty chunks so the schematic world doesn't load real terrain
public class SchematicChunkSource implements IChunkProvider {

    private final WorldClient fallbackWorld;
    private final EmptyChunk emptyChunk;

    public SchematicChunkSource(WorldClient world) {
        this.fallbackWorld = world;
        this.emptyChunk = new EmptyChunk(world, 0, 0);
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        return emptyChunk;
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return emptyChunk;
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {}

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public String makeString() {
        return "SchematicChunkSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        return java.util.Collections.emptyList();
    }

    @Override
    public ChunkPosition func_147416_a(World world, String structureName, int x, int y, int z) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int chunkX, int chunkZ) {}

    @Override
    public void saveExtraData() {}
}
