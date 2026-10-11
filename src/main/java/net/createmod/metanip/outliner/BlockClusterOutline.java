package net.createmod.metanip.outliner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.createmod.metanip.data.Iterate;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

// BlockPos -> int[] {x,y,z}, Direction.Axis -> int (0=X,1=Y,2=Z)
// AxisDirection -> boolean (true=POSITIVE, false=NEGATIVE)

public class BlockClusterOutline extends Outline {

    private final Cluster cluster;

    protected final Vector3f pos0Temp = new Vector3f();
    protected final Vector3f pos1Temp = new Vector3f();
    protected final Vector3f pos2Temp = new Vector3f();
    protected final Vector3f pos3Temp = new Vector3f();
    protected final Vector3f normalTemp = new Vector3f();
    protected final Vector3f originTemp = new Vector3f();

    public BlockClusterOutline(Iterable<int[]> positions) {
        cluster = new Cluster();
        for (int[] pos : positions) cluster.include(pos);
    }

    @Override
    public void render(Matrix4f ms, Vec3 camera, float pt) {
        params.loadColor(colorTemp);
        renderFaces(ms, camera, colorTemp);
        renderEdges(ms, camera, colorTemp, params.disableLineNormals);
    }

    protected void renderFaces(Matrix4f ms, Vec3 camera, Vector4f color) {
        if (params.faceTexturePath == null || cluster.isEmpty()) return;

        GL11.glPushMatrix();
        int[] anchor = cluster.anchor;
        GL11.glTranslatef(anchor[0] - (float)camera.xCoord, anchor[1] - (float)camera.yCoord, anchor[2] - (float)camera.zCoord);

        cluster.visibleFaces.forEach((face, positive) -> {
            ForgeDirection dir = axisAndDirToForge(face.axis, positive);
            int[] pos = face.pos.clone();
            if (positive) {
                pos[0] -= dir.offsetX; pos[1] -= dir.offsetY; pos[2] -= dir.offsetZ;
            }
            bufferBlockFace(pos, dir, color);
        });

        GL11.glPopMatrix();
    }

    protected void renderEdges(Matrix4f ms, Vec3 camera, Vector4f color, boolean disableNormals) {
        float lineWidth = params.getLineWidth();
        if (lineWidth == 0 || cluster.isEmpty()) return;

        GL11.glPushMatrix();
        int[] anchor = cluster.anchor;
        GL11.glTranslatef(anchor[0] - (float)camera.xCoord, anchor[1] - (float)camera.yCoord, anchor[2] - (float)camera.zCoord);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        cluster.visibleEdges.forEach(edge -> {
            int[] pos = edge.pos;
            originTemp.set(pos[0], pos[1], pos[2]);
            ForgeDirection dir = axisAndDirToForge(edge.axis, true);
            bufferCuboidLine(originTemp, dir, 1, lineWidth, color, disableNormals);
        });

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void loadFaceData(ForgeDirection face, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f n) {
        switch (face) {
            case DOWN:
                p0.set(0,0,1); p1.set(0,0,0); p2.set(1,0,0); p3.set(1,0,1); n.set(0,-1,0); break;
            case UP:
                p0.set(0,1,0); p1.set(0,1,1); p2.set(1,1,1); p3.set(1,1,0); n.set(0,1,0); break;
            case NORTH:
                p0.set(1,1,0); p1.set(1,0,0); p2.set(0,0,0); p3.set(0,1,0); n.set(0,0,-1); break;
            case SOUTH:
                p0.set(0,1,1); p1.set(0,0,1); p2.set(1,0,1); p3.set(1,1,1); n.set(0,0,1); break;
            case WEST:
                p0.set(0,1,0); p1.set(0,0,0); p2.set(0,0,1); p3.set(0,1,1); n.set(-1,0,0); break;
            case EAST:
                p0.set(1,1,1); p1.set(1,0,1); p2.set(1,0,0); p3.set(1,1,0); n.set(1,0,0); break;
            default: break;
        }
    }

    protected void bufferBlockFace(int[] pos, ForgeDirection face, Vector4f color) {
        loadFaceData(face, pos0Temp, pos1Temp, pos2Temp, pos3Temp, normalTemp);
        float ox = pos[0] + face.offsetX / 128f;
        float oy = pos[1] + face.offsetY / 128f;
        float oz = pos[2] + face.offsetZ / 128f;
        pos0Temp.add(ox, oy, oz);
        pos1Temp.add(ox, oy, oz);
        pos2Temp.add(ox, oy, oz);
        pos3Temp.add(ox, oy, oz);
        bufferQuad(pos0Temp, pos1Temp, pos2Temp, pos3Temp, color, normalTemp);
    }

    private static ForgeDirection axisAndDirToForge(int axis, boolean positive) {
        switch (axis) {
            case 0: return positive ? ForgeDirection.EAST : ForgeDirection.WEST;
            case 1: return positive ? ForgeDirection.UP : ForgeDirection.DOWN;
            default: return positive ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
        }
    }

    private static class Cluster {
        private int[] anchor;
        private final Map<MergeEntry, Boolean> visibleFaces = new HashMap<>();
        private final Set<MergeEntry> visibleEdges = new HashSet<>();

        public boolean isEmpty() { return anchor == null; }

        public void include(int[] pos) {
            if (anchor == null) anchor = pos.clone();
            int rx = pos[0] - anchor[0], ry = pos[1] - anchor[1], rz = pos[2] - anchor[2];

            // 6 faces
            for (int axis = 0; axis < 3; axis++) {
                for (int offset : Iterate.zeroAndOne) {
                    int[] p = offset(rx, ry, rz, axis, offset);
                    MergeEntry entry = new MergeEntry(axis, p);
                    if (visibleFaces.remove(entry) == null)
                        visibleFaces.put(entry, offset != 0);
                }
            }

            // 12 edges
            for (int axis = 0; axis < 3; axis++) {
                for (int axis2 = 0; axis2 < 3; axis2++) {
                    if (axis == axis2) continue;
                    for (int axis3 = 0; axis3 < 3; axis3++) {
                        if (axis == axis3 || axis2 == axis3) continue;
                        for (int o1 : Iterate.zeroAndOne) {
                            int[] p1 = offset(rx, ry, rz, axis2, o1);
                            for (int o2 : Iterate.zeroAndOne) {
                                int[] p2 = offset(p1[0], p1[1], p1[2], axis3, o2);
                                MergeEntry entry = new MergeEntry(axis, p2);
                                if (!visibleEdges.remove(entry))
                                    visibleEdges.add(entry);
                            }
                        }
                        break;
                    }
                }
            }
        }

        private static int[] offset(int x, int y, int z, int axis, int amount) {
            return new int[]{
                x + (axis == 0 ? amount : 0),
                y + (axis == 1 ? amount : 0),
                z + (axis == 2 ? amount : 0)
            };
        }
    }

    private static class MergeEntry {
        private final int axis;
        private final int[] pos;

        public MergeEntry(int axis, int[] pos) {
            this.axis = axis;
            this.pos = pos.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MergeEntry)) return false;
            MergeEntry other = (MergeEntry) o;
            return axis == other.axis && pos[0] == other.pos[0] && pos[1] == other.pos[1] && pos[2] == other.pos[2];
        }

        @Override
        public int hashCode() {
            return (pos[0] * 31 * 31 + pos[1] * 31 + pos[2]) * 31 + axis;
        }
    }
}
