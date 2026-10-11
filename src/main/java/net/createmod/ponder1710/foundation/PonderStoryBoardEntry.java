package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.registration.StoryBoardEntry;
import net.createmod.ponder1710.api.scene.PonderStoryBoard;

import net.minecraft.util.ResourceLocation;

public class PonderStoryBoardEntry implements StoryBoardEntry {

    private final PonderStoryBoard board;
    private final String namespace;
    @Nullable
    private final ResourceLocation schematicLocation;
    @Nullable
    private final Consumer<PonderLevel> worldSetup;
    private final ResourceLocation component;
    private final List<ResourceLocation> tags;
    private final List<SceneOrderingEntry> orderingEntries;

    // Via schematic file
    public PonderStoryBoardEntry(PonderStoryBoard board, String namespace,
        ResourceLocation schematicLocation, ResourceLocation component) {
        this.board = board;
        this.namespace = namespace;
        this.schematicLocation = schematicLocation;
        this.worldSetup = null;
        this.component = component;
        this.tags = new ArrayList<>();
        this.orderingEntries = new ArrayList<>();
    }

    public PonderStoryBoardEntry(PonderStoryBoard board, String namespace,
        String schematicPath, ResourceLocation component) {
        this(board, namespace, new ResourceLocation(namespace, schematicPath), component);
    }

    // Via code-based world setup
    public PonderStoryBoardEntry(PonderStoryBoard board, String namespace,
        Consumer<PonderLevel> worldSetup, ResourceLocation component) {
        this.board = board;
        this.namespace = namespace;
        this.schematicLocation = null;
        this.worldSetup = worldSetup;
        this.component = component;
        this.tags = new ArrayList<>();
        this.orderingEntries = new ArrayList<>();
    }

    @Override public PonderStoryBoard getBoard()                    { return board; }
    @Override public String getNamespace()                          { return namespace; }
    @Override @Nullable public ResourceLocation getSchematicLocation() { return schematicLocation; }
    @Override @Nullable public Consumer<PonderLevel> getWorldSetup()   { return worldSetup; }
    @Override public ResourceLocation getComponent()               { return component; }
    @Override public List<ResourceLocation> getTags()              { return tags; }
    @Override public List<SceneOrderingEntry> getOrderingEntries() { return orderingEntries; }

    @Override
    public StoryBoardEntry orderBefore(String namespace, String otherSceneId) {
        this.orderingEntries.add(SceneOrderingEntry.before(namespace, otherSceneId));
        return this;
    }

    @Override
    public StoryBoardEntry orderAfter(String namespace, String otherSceneId) {
        this.orderingEntries.add(SceneOrderingEntry.after(namespace, otherSceneId));
        return this;
    }

    @Override
    public StoryBoardEntry highlightTag(ResourceLocation tag) {
        tags.add(tag);
        return this;
    }

    @Override
    public StoryBoardEntry highlightTags(ResourceLocation... tags) {
        Collections.addAll(this.tags, tags);
        return this;
    }

    @Override
    public StoryBoardEntry highlightAllTags() {
        tags.add(PonderTag.Highlight.ALL);
        return this;
    }
}
