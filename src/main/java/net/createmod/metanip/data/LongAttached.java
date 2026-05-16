package net.createmod.metanip.data;

import java.util.Comparator;
import java.util.function.Function;

// Removed: Codec, StreamCodec, ByteBuf (not available in 1.7.10)
// CompoundTag -> NBTTagCompound in 1.7.10
import net.minecraft.nbt.NBTTagCompound;

public class LongAttached<V> extends Pair<Long, V> {

    protected LongAttached(Long first, V second) {
        super(first, second);
    }

    public static <V> LongAttached<V> with(long number, V value) {
        return new LongAttached<>(number, value);
    }

    public static <V> LongAttached<V> withZero(V value) {
        return new LongAttached<>(0L, value);
    }

    public boolean isZero() { return first == 0; }
    public boolean exceeds(long value) { return first > value; }
    public boolean isOrBelowZero() { return first <= 0; }
    public void increment() { first++; }
    public void decrement() { first--; }
    public V getValue() { return getSecond(); }

    public NBTTagCompound serializeNBT(Function<V, NBTTagCompound> serializer) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Item", serializer.apply(getValue()));
        nbt.setLong("Location", getFirst());
        return nbt;
    }

    public static <T> LongAttached<T> read(NBTTagCompound nbt, Function<NBTTagCompound, T> deserializer) {
        return LongAttached.with(nbt.getLong("Location"), deserializer.apply(nbt.getCompoundTag("Item")));
    }

    public static Comparator<? super LongAttached<?>> comparator() {
        return (i1, i2) -> Long.compare(i2.getFirst(), i1.getFirst());
    }
}
