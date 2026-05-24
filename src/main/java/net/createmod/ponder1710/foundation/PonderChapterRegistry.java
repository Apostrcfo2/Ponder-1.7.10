package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// import net.createmod.metanip.data.Pair; // TODO: catnip not available - using simple array
import net.createmod.ponder1710.api.registration.StoryBoardEntry;

// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10
import net.minecraft.util.ResourceLocation;

public class PonderChapterRegistry {

    // Pair from catnip not available - using Object[] {PonderChapter, List<StoryBoardEntry>}
    private final Map<ResourceLocation, Object[]> chapters;

    public PonderChapterRegistry() {
        chapters = new HashMap<>();
    }

    PonderChapter addChapter(@Nonnull PonderChapter chapter) {
        synchronized (chapters) {
            chapters.put(chapter.getId(), new Object[]{chapter, new ArrayList<StoryBoardEntry>()});
        }
        return chapter;
    }

    @Nullable
    PonderChapter getChapter(ResourceLocation id) {
        Object[] pair = chapters.get(id);
        if (pair == null)
            return null;
        return (PonderChapter) pair[0];
    }

    @SuppressWarnings("unchecked")
    public void addStoriesToChapter(@Nonnull PonderChapter chapter, StoryBoardEntry... entries) {
        List<StoryBoardEntry> entryList = (List<StoryBoardEntry>) chapters.get(chapter.getId())[1];
        synchronized (entryList) {
            Collections.addAll(entryList, entries);
        }
    }

    @SuppressWarnings("unchecked")
    public List<PonderChapter> getAllChapters() {
        return chapters.values().stream()
            .map(pair -> (PonderChapter) pair[0])
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<StoryBoardEntry> getStories(PonderChapter chapter) {
        Object[] pair = chapters.get(chapter.getId());
        if (pair == null)
            return new ArrayList<>();
        return (List<StoryBoardEntry>) pair[1];
    }
}
