package net.createmod.metanip.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

// Modern imports replaced:
// BlockPos -> int[] {x,y,z}
// CompoundTag -> NBTTagCompound
// ListTag -> NBTTagList
// FloatTag -> NBTTagFloat
// IntTag -> NBTTagInt
// AABB -> AxisAlignedBB
// Vec3i -> int[] {x,y,z}
// ResourceLocation -> net.minecraft.util.ResourceLocation
// ItemStack -> net.minecraft.item.ItemStack
// NbtUtils -> manual read/write
// HolderLookup.Provider -> removed

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class NBTHelper {

    public static void putMarker(NBTTagCompound nbt, String marker) {
        nbt.setBoolean(marker, true);
    }

    public static int[] readBlockPos(NBTTagCompound nbt, String key) {
        NBTTagCompound pos = nbt.getCompoundTag(key);
        return new int[]{pos.getInteger("X"), pos.getInteger("Y"), pos.getInteger("Z")};
    }

    public static void writeBlockPos(NBTTagCompound nbt, String key, int x, int y, int z) {
        NBTTagCompound pos = new NBTTagCompound();
        pos.setInteger("X", x);
        pos.setInteger("Y", y);
        pos.setInteger("Z", z);
        nbt.setTag(key, pos);
    }

    public static <T extends Enum<?>> T readEnum(NBTTagCompound nbt, String key, Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        if (constants == null)
            throw new IllegalArgumentException("Non-Enum class: " + enumClass.getName());
        if (nbt.hasKey(key, 8)) { // 8 = TAG_String
            String name = nbt.getString(key);
            for (T t : constants)
                if (t.name().equals(name)) return t;
        }
        return constants[0];
    }

    public static <T extends Enum<?>> void writeEnum(NBTTagCompound nbt, String key, T enumConstant) {
        nbt.setString(key, enumConstant.name());
    }

    public static <T> NBTTagList writeCompoundList(Iterable<T> list, Function<T, NBTTagCompound> serializer) {
        NBTTagList listNBT = new NBTTagList();
        for (T t : list) {
            NBTTagCompound tag = serializer.apply(t);
            if (tag != null) listNBT.appendTag(tag);
        }
        return listNBT;
    }

    public static <T> List<T> readCompoundList(NBTTagList listNBT, Function<NBTTagCompound, T> deserializer) {
        List<T> list = new ArrayList<>(listNBT.tagCount());
        for (int i = 0; i < listNBT.tagCount(); i++)
            list.add(deserializer.apply(listNBT.getCompoundTagAt(i)));
        return list;
    }

    public static void iterateCompoundList(NBTTagList listNBT, Consumer<NBTTagCompound> consumer) {
        for (int i = 0; i < listNBT.tagCount(); i++)
            consumer.accept(listNBT.getCompoundTagAt(i));
    }

    public static NBTTagList writeItemList(Iterable<ItemStack> stacks) {
        NBTTagList listNBT = new NBTTagList();
        for (ItemStack stack : stacks) {
            NBTTagCompound tag = new NBTTagCompound();
            if (stack != null) stack.writeToNBT(tag);
            listNBT.appendTag(tag);
        }
        return listNBT;
    }

    public static List<ItemStack> readItemList(NBTTagList stacks) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < stacks.tagCount(); i++)
            list.add(ItemStack.loadItemStackFromNBT(stacks.getCompoundTagAt(i)));
        return list;
    }

    // AxisAlignedBB replaces AABB in 1.7.10
    public static NBTTagList writeAABB(AxisAlignedBB bb) {
        NBTTagList tag = new NBTTagList();
        tag.appendTag(new NBTTagFloat((float) bb.minX));
        tag.appendTag(new NBTTagFloat((float) bb.minY));
        tag.appendTag(new NBTTagFloat((float) bb.minZ));
        tag.appendTag(new NBTTagFloat((float) bb.maxX));
        tag.appendTag(new NBTTagFloat((float) bb.maxY));
        tag.appendTag(new NBTTagFloat((float) bb.maxZ));
        return tag;
    }

    @Nullable
    public static AxisAlignedBB readAABB(NBTTagList tag) {
        if (tag.tagCount() == 0) return null;
        return AxisAlignedBB.getBoundingBox(
            tag.func_150308_e(0), tag.func_150308_e(1), tag.func_150308_e(2),
            tag.func_150308_e(3), tag.func_150308_e(4), tag.func_150308_e(5)
        );
    }

    // Vec3i -> int[] {x,y,z}
    public static NBTTagList writeVec3i(int x, int y, int z) {
        NBTTagList tag = new NBTTagList();
        tag.appendTag(new NBTTagInt(x));
        tag.appendTag(new NBTTagInt(y));
        tag.appendTag(new NBTTagInt(z));
        return tag;
    }

    public static int[] readVec3i(NBTTagList tag) {
        return new int[]{tag.func_150309_d(0), tag.func_150309_d(1), tag.func_150309_d(2)};
    }

    public static NBTTagCompound intToCompound(int i) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("V", i);
        return tag;
    }

    public static int intFromCompound(NBTTagCompound tag) {
        return tag.getInteger("V");
    }

    public static void writeResourceLocation(NBTTagCompound nbt, String key, ResourceLocation loc) {
        nbt.setString(key, loc.toString());
    }

    public static ResourceLocation readResourceLocation(NBTTagCompound nbt, String key) {
        return new ResourceLocation(nbt.getString(key));
    }
}
