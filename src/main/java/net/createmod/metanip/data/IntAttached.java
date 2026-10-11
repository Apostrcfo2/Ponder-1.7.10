package net.createmod.metanip.data;

import java.util.Comparator;
import java.util.function.Function;

// Removed: Codec, StreamCodec, ByteBuf (not available in 1.7.10)
// CompoundTag -> NBTTagCompound in 1.7.10
import net.minecraft.nbt.NBTTagCompound;

public class IntAttached<V> extends Pair<Integer, V> {

    protected IntAttached(Integer first, V second) {
        super(first, second);
    }

    public static <V> IntAttached<V> with(int number, V value) {
        return new IntAttached<>(number, value);
    }

    public static <V> IntAttached<V> withZero(V value) {
        return new IntAttached<>(0, value);
    }

    public boolean isZero() { return first == 0; }
    public boolean exceeds(int value) { return first > value; }
    public boolean isOrBelowZero() { return first <= 0; }
    public void increment() { first++; }
    public void decrement() { first--; }
    public V getValue() { return getSecond(); }

    public NBTTagCompound serializeNBT(Function<V, NBTTagCompound> serializer) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Item", serializer.apply(getValue()));
        nbt.setInteger("Location", getFirst());
        return nbt;
    }

    public static <T> IntAttached<T> read(NBTTagCompound nbt, Function<NBTTagCompound, T> deserializer) {
        return IntAttached.with(nbt.getInteger("Location"), deserializer.apply(nbt.getCompoundTag("Item")));
    }

    public static Comparator<? super IntAttached<?>> comparator() {
        return (i1, i2) -> Integer.compare(i2.getFirst(), i1.getFirst());
    }
}
