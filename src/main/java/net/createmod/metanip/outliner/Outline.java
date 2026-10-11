package net.createmod.metanip.outliner;

import javax.annotation.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

// PoseStack/VertexConsumer not available - using GL11 + JOML directly
// AABB -> AxisAlignedBB, Vec3 -> net.minecraft.util.Vec3
// Direction -> ForgeDirection, LightTexture -> constant

import net.createmod.metanip.math.AngleHelper;
import net.createmod.metanip.theme.Color;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public abstract class Outline {

    protected final OutlineParams params;

    // JOML temps - reused to avoid allocation
    protected final Vector4f colorTemp = new Vector4f();
    protected final Vector3f diffPosTemp = new Vector3f();
    protected final Vector3f minPosTemp = new Vector3f();
    protected final Vector3f maxPosTemp = new Vector3f();
    protected final Vector4f posTransformTemp = new Vector4f();
    protected final Vector3f normalTransformTemp = new Vector3f();

    public Outline() {
        params = new OutlineParams();
    }

    public OutlineParams getParams() { return params; }

    // ms replaced with JOML Matrix4f passed directly
    public abstract void render(Matrix4f ms, Vec3 camera, float pt);

    public void tick() {}

    public void bufferCuboidLine(Matrix4f ms, Vec3 camera, Vector3d start, Vector3d end,
                                  float width, Vector4f color, boolean disableNormals) {
        Vector3f diff = diffPosTemp;
        diff.set((float)(end.x - start.x), (float)(end.y - start.y), (float)(end.z - start.z));

        float length = (float) Math.sqrt(diff.x()*diff.x() + diff.y()*diff.y() + diff.z()*diff.z());
        float hAngle = AngleHelper.deg(Math.atan2(diff.x(), diff.z()));
        float hDistance = (float) Math.sqrt(diff.x()*diff.x() + diff.z()*diff.z());
        float vAngle = AngleHelper.deg(Math.atan2(hDistance, diff.y())) - 90;

        GL11.glPushMatrix();
        GL11.glTranslated(start.x - camera.xCoord, start.y - camera.yCoord, start.z - camera.zCoord);
        GL11.glRotatef(hAngle, 0, 1, 0);
        GL11.glRotatef(vAngle, 1, 0, 0);

        bufferCuboidLine(new Vector3f(), ForgeDirection.SOUTH, length, width, color, disableNormals);

        GL11.glPopMatrix();
    }

    public void bufferCuboidLine(Vector3f origin, ForgeDirection direction,
                                  float length, float width, Vector4f color, boolean disableNormals) {
        Vector3f minPos = minPosTemp;
        Vector3f maxPos = maxPosTemp;

        float hw = width / 2;
        minPos.set(origin.x() - hw, origin.y() - hw, origin.z() - hw);
        maxPos.set(origin.x() + hw, origin.y() + hw, origin.z() + hw);

        switch (direction) {
            case DOWN:  minPos.add(0, -length, 0); break;
            case UP:    maxPos.add(0, length, 0);  break;
            case NORTH: minPos.add(0, 0, -length); break;
            case SOUTH: maxPos.add(0, 0, length);  break;
            case WEST:  minPos.add(-length, 0, 0); break;
            case EAST:  maxPos.add(length, 0, 0);  break;
            default: break;
        }

        bufferCuboid(minPos, maxPos, color, disableNormals);
    }

    // Uses GL11 directly with JOML for math
    public void bufferCuboid(Vector3f minPos, Vector3f maxPos, Vector4f color, boolean disableNormals) {
        float minX = minPos.x(), minY = minPos.y(), minZ = minPos.z();
        float maxX = maxPos.x(), maxY = maxPos.y(), maxZ = maxPos.z();
        float r = color.x(), g = color.y(), b = color.z(), a = color.w();

        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_QUADS);

        // Down
        if (!disableNormals) GL11.glNormal3f(0, -1, 0);
        else GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);

        // Up
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, minZ);

        // North
        if (!disableNormals) GL11.glNormal3f(0, 0, -1);
        else GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, maxY, minZ);

        // South
        if (!disableNormals) GL11.glNormal3f(0, 0, 1);
        else GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);

        // West
        if (!disableNormals) GL11.glNormal3f(-1, 0, 0);
        else GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);

        // East
        if (!disableNormals) GL11.glNormal3f(1, 0, 0);
        else GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);

        GL11.glEnd();
    }

    public void bufferQuad(Vector3f pos0, Vector3f pos1, Vector3f pos2, Vector3f pos3,
                            Vector4f color, Vector3f normal) {
        bufferQuad(pos0, pos1, pos2, pos3, color, 0, 0, 1, 1, normal);
    }

    public void bufferQuad(Vector3f pos0, Vector3f pos1, Vector3f pos2, Vector3f pos3,
                            Vector4f color, float minU, float minV, float maxU, float maxV, Vector3f normal) {
        float r = color.x(), g = color.y(), b = color.z(), a = color.w();

        GL11.glColor4f(r, g, b, a);
        GL11.glNormal3f(normal.x(), normal.y(), normal.z());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(minU, minV); GL11.glVertex3f(pos0.x(), pos0.y(), pos0.z());
        GL11.glTexCoord2f(minU, maxV); GL11.glVertex3f(pos1.x(), pos1.y(), pos1.z());
        GL11.glTexCoord2f(maxU, maxV); GL11.glVertex3f(pos2.x(), pos2.y(), pos2.z());
        GL11.glTexCoord2f(maxU, minV); GL11.glVertex3f(pos3.x(), pos3.y(), pos3.z());
        GL11.glEnd();
    }

    public static class OutlineParams {
        @Nullable
        protected String faceTexturePath;
        @Nullable
        ForgeDirection highlightedFace;
        protected boolean fadeLineWidth;
        protected boolean disableCull;
        protected boolean disableLineNormals;
        protected float alpha;
        protected Color rgb;
        private float lineWidth;

        // Full bright lightmap constant for 1.7.10
        public static final int FULL_BRIGHT = 0xF000F0;

        public OutlineParams() {
            faceTexturePath = null;
            alpha = 1;
            lineWidth = 1 / 32f;
            fadeLineWidth = true;
            rgb = Color.WHITE;
        }

        public OutlineParams colored(int color) {
            rgb = new Color(color, false);
            return this;
        }

        public OutlineParams colored(Color c) {
            rgb = c.copy();
            return this;
        }

        public OutlineParams lineWidth(float width) {
            this.lineWidth = width;
            return this;
        }

        public OutlineParams withFaceTexture(@Nullable String texturePath) {
            this.faceTexturePath = texturePath;
            return this;
        }

        public OutlineParams clearTextures() {
            this.faceTexturePath = null;
            return this;
        }

        public OutlineParams highlightFace(@Nullable ForgeDirection face) {
            highlightedFace = face;
            return this;
        }

        public OutlineParams disableLineNormals() {
            disableLineNormals = true;
            return this;
        }

        public OutlineParams disableCull() {
            disableCull = true;
            return this;
        }

        public float getLineWidth() {
            return fadeLineWidth ? alpha * lineWidth : lineWidth;
        }

        @Nullable
        public ForgeDirection getHighlightedFace() { return highlightedFace; }

        public void loadColor(Vector4f vec) {
            vec.set(rgb.getRedAsFloat(), rgb.getGreenAsFloat(), rgb.getBlueAsFloat(), rgb.getAlphaAsFloat() * alpha);
        }
    }
}
