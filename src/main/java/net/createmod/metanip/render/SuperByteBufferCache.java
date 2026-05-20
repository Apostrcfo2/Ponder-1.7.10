package net.createmod.metanip.render;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// Simplified cache for SuperByteBuffer instances in 1.7.10.
// In modern MC this caches vertex buffer data by render layer.
// In 1.7.10 we cache display lists or just re-render each frame.

public class SuperByteBufferCache {

    private final Map<Object, Integer> displayLists = new HashMap<>();

    public void invalidate(Object key) {
        Integer list = displayLists.remove(key);
        if (list != null) {
            // GL11.glDeleteLists(list, 1); // call from GL thread
        }
    }

    public void invalidateAll() {
        displayLists.clear();
    }

    public boolean has(Object key) {
        return displayLists.containsKey(key);
    }

    public void put(Object key, int displayList) {
        displayLists.put(key, displayList);
    }

    public int get(Object key) {
        return displayLists.getOrDefault(key, -1);
    }
}
