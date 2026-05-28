package net.createmod.ponder1710.foundation.registration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.registration.SceneRegistryAccess;
import net.createmod.ponder1710.api.registration.StoryBoardEntry;
import net.createmod.ponder1710.api.scene.SceneBuilder;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class PonderSceneRegistry implements SceneRegistryAccess {

    private final PonderLocalization localization;
    private final Multimap<ResourceLocation, StoryBoardEntry> scenes;
    private boolean allowRegistration = true;

    public PonderSceneRegistry(PonderLocalization localization) {
        this.localization = localization;
        scenes = LinkedHashMultimap.create();
    }

    public void clearRegistry() {
        scenes.clear();
        allowRegistration = true;
    }

    public void addStoryBoard(StoryBoardEntry entry) {
        if (!allowRegistration)
            throw new IllegalStateException("Registration Phase has already ended!");
        scenes.put(entry.getComponent(), entry);
    }

    @Override
    public Collection<Map.Entry<ResourceLocation, StoryBoardEntry>> getRegisteredEntries() {
        return scenes.entries();
    }

    @Override
    public boolean doScenesExistForId(ResourceLocation id) {
        return scenes.containsKey(id);
    }

    @Override
    public List<PonderScene> compile(ResourceLocation id) {
        if (PonderIndex.editingModeActive()) PonderIndex.reload();
        Collection<StoryBoardEntry> entries = scenes.get(id);
        if (entries.isEmpty()) return Collections.emptyList();
        return compile(entries);
    }

    @Override
    public List<PonderScene> compile(Collection<StoryBoardEntry> entries) {
        if (PonderIndex.editingModeActive()) {
            localization.clearShared();
            PonderIndex.gatherSharedText();
        }

        List<PonderScene> result = new ArrayList<>();
        int subWorldId = 1000;

        for (StoryBoardEntry storyBoard : entries) {
            WorldClient parent = Minecraft.getMinecraft().theWorld;
            PonderLevel level = new PonderLevel(parent, subWorldId++);

            // Load schematic into level
            ResourceLocation schematicLoc = storyBoard.getSchematicLocation();
            if (schematicLoc != null) {
                try {
                    loadSchematic(schematicLoc, level);
                } catch (Exception e) {
                    Ponder.LOGGER.warn("Failed to load schematic {}: {}", schematicLoc, e.getMessage());
                }
            }

            level.createBackup();

            PonderScene scene = compileScene(localization, storyBoard, level);
            scene.begin();
            result.add(scene);
        }
        return result;
    }

    public static PonderScene compileScene(PonderLocalization localization, StoryBoardEntry sb,
        @Nullable PonderLevel level) {
        PonderScene scene = new PonderScene(level, localization, sb.getNamespace(),
            sb.getComponent(), sb.getTags(), sb.getOrderingEntries());
        SceneBuilder builder = scene.builder();
        sb.getBoard().program(builder, scene.getSceneBuildingUtil());
        return scene;
    }

    // Load a 1.7.10-compatible .nbt schematic into a PonderLevel
    // Schematic format: NBTTagCompound with Width, Height, Length, Blocks[], Data[], TileEntities[]
    public static void loadSchematic(ResourceLocation location, PonderLevel level) throws Exception {
        String path = "/assets/" + location.getResourceDomain()
            + "/ponder/" + location.getResourcePath() + ".nbt";

        InputStream stream = PonderSceneRegistry.class.getResourceAsStream(path);
        if (stream == null) {
            Ponder.LOGGER.warn("Schematic not found: {}", path);
            return;
        }

        NBTTagCompound nbt = CompressedStreamTools.readCompressed(stream);
        stream.close();

        int width  = nbt.getShort("Width");
        int height = nbt.getShort("Height");
        int length = nbt.getShort("Length");

        byte[] blockIds   = nbt.getByteArray("Blocks");
        byte[] blockMetas = nbt.getByteArray("Data");

        // Place blocks
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index = (y * length + z) * width + x;
                    int blockId = blockIds[index] & 0xFF;
                    int meta    = blockMetas[index] & 0xFF;
                    Block block = Block.getBlockById(blockId);
                    if (block != null)
                        level.setBlock(x, y, z, block, meta, 2);
                }
            }
        }

        // Place TileEntities
        if (nbt.hasKey("TileEntities")) {
            NBTTagList teList = nbt.getTagList("TileEntities", 10);
            for (int i = 0; i < teList.tagCount(); i++) {
                NBTTagCompound teNbt = teList.getCompoundTagAt(i);
                try {
                    TileEntity te = TileEntity.createAndLoadEntity(teNbt);
                    if (te != null) {
                        level.setTileEntity(te.xCoord, te.yCoord, te.zCoord, te);
                    }
                } catch (Exception e) {
                    Ponder.LOGGER.debug("Failed to load TileEntity from schematic", e);
                }
            }
        }

        // Set bounds on the level
        level.setBounds(new int[]{0, 0, 0, width-1, height-1, length-1});
        Ponder.LOGGER.debug("Loaded schematic {} ({}x{}x{})", location, width, height, length);
    }
}
