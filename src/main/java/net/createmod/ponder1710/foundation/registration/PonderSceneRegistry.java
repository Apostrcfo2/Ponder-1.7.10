package net.createmod.ponder1710.foundation.registration;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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

// import net.minecraft.client.Minecraft; // different in 1.7.10
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.registries.BuiltInRegistries; // not available in 1.7.10
// import net.minecraft.nbt.CompoundTag; // NBTTagCompound in 1.7.10
// import net.minecraft.nbt.NbtAccounter; // not available in 1.7.10
// import net.minecraft.nbt.NbtIo; // not available in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
// import net.minecraft.server.packs.resources.Resource; // not available in 1.7.10
// import net.minecraft.server.packs.resources.ResourceManager; // different in 1.7.10
// import net.minecraft.world.level.block.Block; // different package in 1.7.10
// import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings; // not available
// import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate; // not available

import net.minecraft.nbt.NBTTagCompound;
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
        if (PonderIndex.editingModeActive())
            PonderIndex.reload();
        Collection<StoryBoardEntry> entries = scenes.get(id);
        if (entries.isEmpty())
            return Collections.emptyList();
        return compile(entries);
    }

    @Override
    public List<PonderScene> compile(Collection<StoryBoardEntry> entries) {
        if (PonderIndex.editingModeActive()) {
            localization.clearShared();
            PonderIndex.gatherSharedText();
        }

        List<PonderScene> scenes = new ArrayList<>();
        for (StoryBoardEntry storyBoard : entries) {
            // TODO: loadSchematic - StructureTemplate not available in 1.7.10
            // Will need to implement using 1.7.10 NBT schematic system
            PonderLevel level = new PonderLevel(null);
            PonderScene scene = compileScene(localization, storyBoard, level);
            scene.begin();
            scenes.add(scene);
        }
        return scenes;
    }

    public static PonderScene compileScene(PonderLocalization localization, StoryBoardEntry sb, @Nullable PonderLevel level) {
        PonderScene scene = new PonderScene(level, localization, sb.getNamespace(), sb.getComponent(),
            sb.getTags(), sb.getOrderingEntries());
        SceneBuilder builder = scene.builder();
        sb.getBoard().program(builder, scene.getSceneBuildingUtil());
        return scene;
    }

    // TODO: loadSchematic - StructureTemplate/ResourceManager not available in 1.7.10
    // Will need to reimplement using 1.7.10 resource system and NBT schematic format
    // public static void loadSchematic(ResourceLocation location) { ... }
}
