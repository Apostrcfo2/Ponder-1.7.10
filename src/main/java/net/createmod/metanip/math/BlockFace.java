package net.createmod.metanip.math;

// import com.mojang.serialization.Codec; // not available in 1.7.10
// import io.netty.buffer.ByteBuf; // not available in 1.7.10
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.nbt.CompoundTag; // NBTTagCompound in 1.7.10
// import net.minecraft.nbt.NbtUtils; // not available in 1.7.10
// import net.createmod.metanip.nbt.NBTHelper; // TODO: port later

import net.createmod.metanip.data.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

// BlockPos replaced with int[] {x, y, z}
public class BlockFace extends Pair<int[], ForgeDirection> {

    public BlockFace(int[] pos, ForgeDirection face) {
        super(pos, face);
    }

    public boolean isEquivalent(BlockFace other) {
        if (equals(other)) return true;
        int[] connected = getConnectedPos();
        int[] otherConnected = other.getConnectedPos();
        return java.util.Arrays.equals(connected, other.getPos())
            && java.util.Arrays.equals(getPos(), otherConnected);
    }

    public int[] getPos() { return getFirst(); }

    public ForgeDirection getFace() { return getSecond(); }

    public ForgeDirection getOppositeFace() { return getSecond().getOpposite(); }

    public BlockFace getOpposite() { return new BlockFace(getConnectedPos(), getOppositeFace()); }

    public int[] getConnectedPos() {
        int[] pos = getPos();
        ForgeDirection face = getFace();
        return new int[]{pos[0] + face.offsetX, pos[1] + face.offsetY, pos[2] + face.offsetZ};
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        int[] pos = getPos();
        nbt.setInteger("PosX", pos[0]);
        nbt.setInteger("PosY", pos[1]);
        nbt.setInteger("PosZ", pos[2]);
        nbt.setInteger("Face", getFace().ordinal());
        return nbt;
    }

    public static BlockFace fromNBT(NBTTagCompound nbt) {
        int[] pos = {nbt.getInteger("PosX"), nbt.getInteger("PosY"), nbt.getInteger("PosZ")};
        ForgeDirection face = ForgeDirection.values()[nbt.getInteger("Face")];
        return new BlockFace(pos, face);
    }
}
