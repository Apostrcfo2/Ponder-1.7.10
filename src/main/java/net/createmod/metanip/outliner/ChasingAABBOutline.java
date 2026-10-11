package net.createmod.metanip.outliner;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class ChasingAABBOutline extends AABBOutline {

    AxisAlignedBB targetBB;
    AxisAlignedBB prevBB;

    public ChasingAABBOutline(AxisAlignedBB bb) {
        super(bb);
        prevBB = bb;
        targetBB = bb;
    }

    public void target(AxisAlignedBB target) { targetBB = target; }

    @Override
    public void tick() {
        prevBB = bb;
        setBounds(interpolateBBs(bb, targetBB, .5f));
    }

    @Override
    public void render(Matrix4f ms, Vec3 camera, float pt) {
        params.loadColor(colorTemp);
        renderBox(ms, camera, interpolateBBs(prevBB, bb, pt), colorTemp, params.disableLineNormals);
    }

    private static AxisAlignedBB interpolateBBs(AxisAlignedBB current, AxisAlignedBB target, float pt) {
        return AxisAlignedBB.getBoundingBox(
            current.minX + (target.minX - current.minX) * pt,
            current.minY + (target.minY - current.minY) * pt,
            current.minZ + (target.minZ - current.minZ) * pt,
            current.maxX + (target.maxX - current.maxX) * pt,
            current.maxY + (target.maxY - current.maxY) * pt,
            current.maxZ + (target.maxZ - current.maxZ) * pt
        );
    }
}
