package net.createmod.metanip.outliner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class AABBOutline extends Outline {

    protected AxisAlignedBB bb;

    protected final Vector3f minPosTemp1 = new Vector3f();
    protected final Vector3f maxPosTemp1 = new Vector3f();
    protected final Vector4f colorTemp1 = new Vector4f();
    protected final Vector3f pos0Temp = new Vector3f();
    protected final Vector3f pos1Temp = new Vector3f();
    protected final Vector3f pos2Temp = new Vector3f();
    protected final Vector3f pos3Temp = new Vector3f();
    protected final Vector3f normalTemp = new Vector3f();
    protected final Vector3f originTemp = new Vector3f();

    public AABBOutline(AxisAlignedBB bb) { setBounds(bb); }

    public AxisAlignedBB getBounds() { return bb; }
    public void setBounds(AxisAlignedBB bb) { this.bb = bb; }

    @Override
    public void render(Matrix4f ms, Vec3 camera, float pt) {
        params.loadColor(colorTemp);
        renderBox(ms, camera, bb, colorTemp, params.disableLineNormals);
    }

    protected void renderBox(Matrix4f ms, Vec3 camera, AxisAlignedBB box, Vector4f color, boolean disableLineNormals) {
        boolean cameraInside = camera.xCoord >= box.minX && camera.xCoord <= box.maxX
            && camera.yCoord >= box.minY && camera.yCoord <= box.maxY
            && camera.zCoord >= box.minZ && camera.zCoord <= box.maxZ;
        float inflate = cameraInside ? -1/128f : 1/128f;

        // Offset by camera
        float minX = (float)(box.minX - camera.xCoord) - inflate;
        float minY = (float)(box.minY - camera.yCoord) - inflate;
        float minZ = (float)(box.minZ - camera.zCoord) - inflate;
        float maxX = (float)(box.maxX - camera.xCoord) + inflate;
        float maxY = (float)(box.maxY - camera.yCoord) + inflate;
        float maxZ = (float)(box.maxZ - camera.zCoord) + inflate;

        minPosTemp1.set(minX, minY, minZ);
        maxPosTemp1.set(maxX, maxY, maxZ);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        if (!cameraInside && !params.disableCull)
            GL11.glEnable(GL11.GL_CULL_FACE);

        // Render faces if texture set
        if (params.faceTexturePath != null)
            renderBoxFaces(minPosTemp1, maxPosTemp1, color);

        // Render edges
        float lineWidth = params.getLineWidth();
        if (lineWidth > 0) {
            GL11.glLineWidth(Math.max(1, (int)(lineWidth * 32)));
            renderBoxEdges(minPosTemp1, maxPosTemp1, lineWidth, color, disableLineNormals);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    protected void renderBoxFaces(Vector3f minPos, Vector3f maxPos, Vector4f color) {
        for (ForgeDirection face : new ForgeDirection[]{
            ForgeDirection.DOWN, ForgeDirection.UP,
            ForgeDirection.NORTH, ForgeDirection.SOUTH,
            ForgeDirection.WEST, ForgeDirection.EAST
        }) {
            renderBoxFace(minPos, maxPos, face, color);
        }
    }

    protected void renderBoxFace(Vector3f minPos, Vector3f maxPos, ForgeDirection face, Vector4f color) {
        float minX = minPos.x(), minY = minPos.y(), minZ = minPos.z();
        float maxX = maxPos.x(), maxY = maxPos.y(), maxZ = maxPos.z();

        boolean highlighted = face == params.highlightedFace;
        float alphaMult = highlighted ? 1 : 0.5f;
        colorTemp1.set(color.x(), color.y(), color.z(), color.w() * alphaMult);

        Vector3f p0 = pos0Temp, p1 = pos1Temp, p2 = pos2Temp, p3 = pos3Temp;
        Vector3f n = normalTemp;

        switch (face) {
            case DOWN:
                p0.set(minX, minY, maxZ); p1.set(minX, minY, minZ);
                p2.set(maxX, minY, minZ); p3.set(maxX, minY, maxZ);
                n.set(0, -1, 0); break;
            case UP:
                p0.set(minX, maxY, minZ); p1.set(minX, maxY, maxZ);
                p2.set(maxX, maxY, maxZ); p3.set(maxX, maxY, minZ);
                n.set(0, 1, 0); break;
            case NORTH:
                p0.set(maxX, maxY, minZ); p1.set(maxX, minY, minZ);
                p2.set(minX, minY, minZ); p3.set(minX, maxY, minZ);
                n.set(0, 0, -1); break;
            case SOUTH:
                p0.set(minX, maxY, maxZ); p1.set(minX, minY, maxZ);
                p2.set(maxX, minY, maxZ); p3.set(maxX, maxY, maxZ);
                n.set(0, 0, 1); break;
            case WEST:
                p0.set(minX, maxY, minZ); p1.set(minX, minY, minZ);
                p2.set(minX, minY, maxZ); p3.set(minX, maxY, maxZ);
                n.set(-1, 0, 0); break;
            case EAST:
                p0.set(maxX, maxY, maxZ); p1.set(maxX, minY, maxZ);
                p2.set(maxX, minY, minZ); p3.set(maxX, maxY, minZ);
                n.set(1, 0, 0); break;
            default: return;
        }

        bufferQuad(p0, p1, p2, p3, colorTemp1, n);
    }

    protected void renderBoxEdges(Vector3f minPos, Vector3f maxPos, float lineWidth, Vector4f color, boolean disableNormals) {
        Vector3f origin = originTemp;

        float lx = maxPos.x() - minPos.x();
        float ly = maxPos.y() - minPos.y();
        float lz = maxPos.z() - minPos.z();

        origin.set(minPos);
        bufferCuboidLine(origin, ForgeDirection.EAST, lx, lineWidth, color, disableNormals);
        bufferCuboidLine(origin, ForgeDirection.UP, ly, lineWidth, color, disableNormals);
        bufferCuboidLine(origin, ForgeDirection.SOUTH, lz, lineWidth, color, disableNormals);

        origin.set(maxPos.x(), minPos.y(), minPos.z());
        bufferCuboidLine(origin, ForgeDirection.UP, ly, lineWidth, color, disableNormals);
        bufferCuboidLine(origin, ForgeDirection.SOUTH, lz, lineWidth, color, disableNormals);

        origin.set(minPos.x(), maxPos.y(), minPos.z());
        bufferCuboidLine(origin, ForgeDirection.EAST, lx, lineWidth, color, disableNormals);
        bufferCuboidLine(origin, ForgeDirection.SOUTH, lz, lineWidth, color, disableNormals);

        origin.set(minPos.x(), minPos.y(), maxPos.z());
        bufferCuboidLine(origin, ForgeDirection.EAST, lx, lineWidth, color, disableNormals);
        bufferCuboidLine(origin, ForgeDirection.UP, ly, lineWidth, color, disableNormals);

        origin.set(minPos.x(), maxPos.y(), maxPos.z());
        bufferCuboidLine(origin, ForgeDirection.EAST, lx, lineWidth, color, disableNormals);

        origin.set(maxPos.x(), minPos.y(), maxPos.z());
        bufferCuboidLine(origin, ForgeDirection.UP, ly, lineWidth, color, disableNormals);

        origin.set(maxPos.x(), maxPos.y(), minPos.z());
        bufferCuboidLine(origin, ForgeDirection.SOUTH, lz, lineWidth, color, disableNormals);
    }
}
