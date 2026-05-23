package net.createmod.metanip.math;

import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.core.Direction.Axis; // not available
// import net.minecraft.core.Vec3i; // not available
// import net.minecraft.nbt.CompoundTag; // NBTTagCompound in 1.7.10
// import net.minecraft.nbt.DoubleTag; // not available in 1.7.10
// import net.minecraft.nbt.ListTag; // NBTTagList in 1.7.10
// import net.minecraft.network.FriendlyByteBuf; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.util.RandomSource; // Random in 1.7.10
// import net.minecraft.world.entity.Entity; // different package in 1.7.10
// import net.minecraft.world.entity.player.Player; // EntityPlayer in 1.7.10
// import net.minecraft.world.level.block.Mirror; // not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
// import net.createmod.ponder1710.mixin.client.accessor.GameRendererAccessor; // not available
// import net.minecraft.client.Camera; // not available in 1.7.10

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class VecHelper {

    public static final Vec3 CENTER_OF_ORIGIN = Vec3.createVectorHelper(.5, .5, .5);

    // Axis constants as ints: 0=X, 1=Y, 2=Z
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    public static Vec3 rotate(Vec3 vec, double xRot, double yRot, double zRot) {
        return rotate(rotate(rotate(vec, xRot, AXIS_X), yRot, AXIS_Y), zRot, AXIS_Z);
    }

    public static Vec3 rotateCentered(Vec3 vec, double deg, int axis) {
        Vec3 shift = getCenterOf(0, 0, 0);
        return VecHelper.rotate(
            Vec3.createVectorHelper(vec.xCoord - shift.xCoord, vec.yCoord - shift.yCoord, vec.zCoord - shift.zCoord),
            deg, axis
        ).addVector(shift.xCoord, shift.yCoord, shift.zCoord);
    }

    public static Vec3 rotate(Vec3 vec, double deg, int axis) {
        if (deg == 0) return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = vec.xCoord, y = vec.yCoord, z = vec.zCoord;

        if (axis == AXIS_X)
            return Vec3.createVectorHelper(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == AXIS_Y)
            return Vec3.createVectorHelper(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == AXIS_Z)
            return Vec3.createVectorHelper(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }

    public static Vec3 getCenterOf(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return CENTER_OF_ORIGIN;
        return Vec3.createVectorHelper(x + .5, y + .5, z + .5);
    }

    public static Vec3 offsetRandomly(Vec3 vec, Random r, float radius) {
        return Vec3.createVectorHelper(
            vec.xCoord + (r.nextFloat() - .5f) * 2 * radius,
            vec.yCoord + (r.nextFloat() - .5f) * 2 * radius,
            vec.zCoord + (r.nextFloat() - .5f) * 2 * radius
        );
    }

    public static Vec3 axisAlingedPlaneOf(ForgeDirection face) {
        return Vec3.createVectorHelper(
            1 - Math.abs(face.offsetX),
            1 - Math.abs(face.offsetY),
            1 - Math.abs(face.offsetZ)
        );
    }

    public static NBTTagList writeNBT(Vec3 vec) {
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagDouble(vec.xCoord));
        list.appendTag(new NBTTagDouble(vec.yCoord));
        list.appendTag(new NBTTagDouble(vec.zCoord));
        return list;
    }

    public static NBTTagCompound writeNBTCompound(Vec3 vec) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("V", writeNBT(vec));
        return nbt;
    }

    public static Vec3 readNBT(NBTTagList list) {
        if (list.tagCount() == 0) return Vec3.createVectorHelper(0, 0, 0);
        return Vec3.createVectorHelper(list.func_150309_d(0), list.func_150309_d(1), list.func_150309_d(2));
    }

    public static Vec3 readNBTCompound(NBTTagCompound nbt) {
        return readNBT((NBTTagList) nbt.getTag("V"));
    }

    public static Vec3 voxelSpace(double x, double y, double z) {
        return Vec3.createVectorHelper(x / 16f, y / 16f, z / 16f);
    }

    public static float getCoordinate(Vec3 vec, int axis) {
        if (axis == AXIS_X) return (float) vec.xCoord;
        if (axis == AXIS_Y) return (float) vec.yCoord;
        return (float) vec.zCoord;
    }

    public static Vec3 clamp(Vec3 vec, float maxLength) {
        double lenSq = vec.xCoord*vec.xCoord + vec.yCoord*vec.yCoord + vec.zCoord*vec.zCoord;
        if (lenSq > maxLength * maxLength) {
            double len = Math.sqrt(lenSq);
            return Vec3.createVectorHelper(vec.xCoord/len*maxLength, vec.yCoord/len*maxLength, vec.zCoord/len*maxLength);
        }
        return vec;
    }

    public static Vec3 lerp(float p, Vec3 from, Vec3 to) {
        return Vec3.createVectorHelper(
            from.xCoord + (to.xCoord - from.xCoord) * p,
            from.yCoord + (to.yCoord - from.yCoord) * p,
            from.zCoord + (to.zCoord - from.zCoord) * p
        );
    }

    public static Vec3 project(Vec3 vec, Vec3 ontoVec) {
        double lenSq = ontoVec.xCoord*ontoVec.xCoord + ontoVec.yCoord*ontoVec.yCoord + ontoVec.zCoord*ontoVec.zCoord;
        if (lenSq == 0) return Vec3.createVectorHelper(0, 0, 0);
        double dot = vec.xCoord*ontoVec.xCoord + vec.yCoord*ontoVec.yCoord + vec.zCoord*ontoVec.zCoord;
        double scale = dot / lenSq;
        return Vec3.createVectorHelper(ontoVec.xCoord*scale, ontoVec.yCoord*scale, ontoVec.zCoord*scale);
    }

    public static boolean isVecPointingTowards(Vec3 vec, ForgeDirection direction) {
        double dot = direction.offsetX * vec.xCoord + direction.offsetY * vec.yCoord + direction.offsetZ * vec.zCoord;
        double len = Math.sqrt(vec.xCoord*vec.xCoord + vec.yCoord*vec.yCoord + vec.zCoord*vec.zCoord);
        return len > 0 && dot / len > 0.125;
    }

    public static Vec3 bezier(Vec3 p1, Vec3 p2, Vec3 q1, Vec3 q2, float t) {
        Vec3 v1 = lerp(t, p1, q1);
        Vec3 v2 = lerp(t, q1, q2);
        Vec3 v3 = lerp(t, q2, p2);
        Vec3 inner1 = lerp(t, v1, v2);
        Vec3 inner2 = lerp(t, v2, v3);
        return lerp(t, inner1, inner2);
    }






}

    // Previously TODO - now implemented for 1.7.10

    // slerp - spherical lerp using JOML-style math
    public static Vec3 slerp(float p, Vec3 from, Vec3 to) {
        double dot = from.xCoord*to.xCoord + from.yCoord*to.yCoord + from.zCoord*to.zCoord;
        dot = Math.max(-1, Math.min(1, dot));
        double theta = Math.acos(dot) * p;
        double sin1 = Math.sin(theta);
        double sin2 = Math.sin(Math.acos(dot));
        if (Math.abs(sin2) < 1e-6) return lerp(p, from, to);
        double s1 = Math.cos(theta) - dot * sin1 / sin2;
        double s2 = sin1 / sin2;
        return Vec3.createVectorHelper(
            s1*from.xCoord + s2*to.xCoord,
            s1*from.yCoord + s2*to.yCoord,
            s1*from.zCoord + s2*to.zCoord
        );
    }

    // mirror - replaces Mirror enum with int (0=NONE, 1=LEFT_RIGHT, 2=FRONT_BACK)
    public static Vec3 mirror(Vec3 vec, int mirror) {
        if (mirror == 0) return vec; // NONE
        if (mirror == 1) return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, -vec.zCoord); // LEFT_RIGHT
        if (mirror == 2) return Vec3.createVectorHelper(-vec.xCoord, vec.yCoord, vec.zCoord); // FRONT_BACK
        return vec;
    }

    public static Vec3 mirrorCentered(Vec3 vec, int mirror) {
        Vec3 shift = getCenterOf(0, 0, 0);
        return VecHelper.mirror(
            Vec3.createVectorHelper(vec.xCoord - shift.xCoord, vec.yCoord - shift.yCoord, vec.zCoord - shift.zCoord),
            mirror
        ).addVector(shift.xCoord, shift.yCoord, shift.zCoord);
    }

    // intersect - axis as int (0=X, 1=Y, 2=Z)
    public static double[] intersect(Vec3 p1, Vec3 p2, Vec3 r, Vec3 s, int plane) {
        double p1x = p1.xCoord, p1y = p1.yCoord, p1z = p1.zCoord;
        double p2x = p2.xCoord, p2y = p2.yCoord, p2z = p2.zCoord;
        double rx = r.xCoord, ry = r.yCoord, rz = r.zCoord;
        double sx = s.xCoord, sy = s.yCoord, sz = s.zCoord;

        if (plane == 0) { // X axis - use Y,Z plane
            p1x = p1y; p1y = 0; p1z = p1z;
            p2x = p2y; p2y = 0; p2z = p2z;
            rx = ry; ry = 0; rz = rz;
            sx = sy; sy = 0; sz = sz;
        } else if (plane == 2) { // Z axis - use X,Y plane
            p1z = p1y; p1y = 0;
            p2z = p2y; p2y = 0;
            rz = ry; ry = 0;
            sz = sy; sy = 0;
        }

        double qx = p2x - p1x, qz = p2z - p1z;
        double rcs = rx * sz - rz * sx;
        if (Math.abs(rcs) < 1e-10) return null;

        double t = (qx * sz - qz * sx) / rcs;
        double u = (qx * rz - qz * rx) / rcs;
        return new double[]{t, u};
    }

    public static double[] intersectRanged(Vec3 p1, Vec3 q1, Vec3 p2, Vec3 q2, int plane) {
        Vec3 pDiff = Vec3.createVectorHelper(q1.xCoord-p1.xCoord, q1.yCoord-p1.yCoord, q1.zCoord-p1.zCoord);
        Vec3 qDiff = Vec3.createVectorHelper(q2.xCoord-p2.xCoord, q2.yCoord-p2.yCoord, q2.zCoord-p2.zCoord);
        double pLen = Math.sqrt(pDiff.xCoord*pDiff.xCoord + pDiff.yCoord*pDiff.yCoord + pDiff.zCoord*pDiff.zCoord);
        double qLen = Math.sqrt(qDiff.xCoord*qDiff.xCoord + qDiff.yCoord*qDiff.yCoord + qDiff.zCoord*qDiff.zCoord);
        Vec3 pNorm = pLen > 0 ? Vec3.createVectorHelper(pDiff.xCoord/pLen, pDiff.yCoord/pLen, pDiff.zCoord/pLen) : pDiff;
        Vec3 qNorm = qLen > 0 ? Vec3.createVectorHelper(qDiff.xCoord/qLen, qDiff.yCoord/qLen, qDiff.zCoord/qLen) : qDiff;
        double[] intersect = intersect(p1, p2, pNorm, qNorm, plane);
        if (intersect == null) return null;
        if (intersect[0] < 0 || intersect[1] < 0) return null;
        if (intersect[0]*intersect[0] > pLen*pLen || intersect[1]*intersect[1] > qLen*qLen) return null;
        return intersect;
    }

    // alignedDistanceToFace - uses ForgeDirection instead of Direction.Axis
    public static double alignedDistanceToFace(Vec3 pos, int bx, int by, int bz, ForgeDirection face) {
        double coord, blockCoord;
        int positive = (face.offsetX > 0 || face.offsetY > 0 || face.offsetZ > 0) ? 1 : 0;
        if (face.offsetX != 0) { coord = pos.xCoord; blockCoord = bx + positive; }
        else if (face.offsetY != 0) { coord = pos.yCoord; blockCoord = by + positive; }
        else { coord = pos.zCoord; blockCoord = bz + positive; }
        return Math.abs(coord - blockCoord);
    }

    // projectToPlayerView - approximation without Camera class
    public static Vec3 projectToPlayerView(Vec3 target, float partialTicks) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity viewer = mc.renderViewEntity;
        double cx = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * partialTicks;
        double cy = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * partialTicks + viewer.getEyeHeight();
        double cz = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * partialTicks;

        float yaw = (float) Math.toRadians(viewer.prevRotationYaw + (viewer.rotationYaw - viewer.prevRotationYaw) * partialTicks);
        float pitch = (float) Math.toRadians(viewer.prevRotationPitch + (viewer.rotationPitch - viewer.prevRotationPitch) * partialTicks);

        double dx = target.xCoord - cx, dy = target.yCoord - cy, dz = target.zCoord - cz;

        double sinY = Math.sin(yaw), cosY = Math.cos(yaw);
        double sinP = Math.sin(pitch), cosP = Math.cos(pitch);

        double rx = dx * cosY - dz * sinY;
        double ry = dy * cosP + (dx * sinY + dz * cosY) * sinP;
        double rz = -dy * sinP + (dx * sinY + dz * cosY) * cosP;

        float fov = mc.gameSettings.fovSetting;
        float halfH = mc.displayHeight / 2f;
        float scale = halfH / (float)(rz * Math.tan(Math.toRadians(fov / 2)));

        return Vec3.createVectorHelper(-(float)rx * scale, (float)ry * scale, rz);
    }
