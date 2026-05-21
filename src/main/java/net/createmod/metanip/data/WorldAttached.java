package net.createmod.metanip.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.world.World;

// LevelAccessor -> World in 1.7.10
// var -> explicit type for Java 8
public class WorldAttached<T> {

    static List<WeakReference<Map<World, ?>>> allMaps = new ArrayList<>();

    private final Map<World, T> attached;
    private final Function<World, T> factory;

    public WorldAttached(Function<World, T> factory) {
        this.factory = factory;
        attached = new WeakHashMap<>();
        allMaps.add(new WeakReference<>(attached));
    }

    public static void invalidateWorld(World world) {
        java.util.Iterator<WeakReference<Map<World, ?>>> i = allMaps.iterator();
        while (i.hasNext()) {
            Map<World, ?> map = i.next().get();
            if (map == null) i.remove();
            else map.remove(world);
        }
    }

    @Nonnull
    public T get(World world) {
        T t = attached.get(world);
        if (t != null) return t;
        T entry = factory.apply(world);
        put(world, entry);
        return entry;
    }

    public void put(World world, T entry) { attached.put(world, entry); }

    @Nonnull
    public T replace(World world) {
        attached.remove(world);
        return get(world);
    }

    @Nonnull
    public T replace(World world, Consumer<T> finalizer) {
        T removed = attached.remove(world);
        if (removed != null) finalizer.accept(removed);
        return get(world);
    }

    public void empty(BiConsumer<World, T> finalizer) {
        attached.forEach(finalizer);
        attached.clear();
    }

    public void empty(Consumer<T> finalizer) {
        attached.values().forEach(finalizer);
        attached.clear();
    }
}
