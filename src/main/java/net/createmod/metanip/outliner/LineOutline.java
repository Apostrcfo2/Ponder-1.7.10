package net.createmod.metanip.outliner;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import net.minecraft.util.Vec3;

public class LineOutline extends Outline {

    protected final Vector3d start = new Vector3d(0, 0, 0);
    protected final Vector3d end = new Vector3d(0, 0, 0);

    public LineOutline set(Vector3d start, Vector3d end) {
        this.start.set(start); this.end.set(end);
        return this;
    }

    public LineOutline set(Vec3 start, Vec3 end) {
        this.start.set(start.xCoord, start.yCoord, start.zCoord);
        this.end.set(end.xCoord, end.yCoord, end.zCoord);
        return this;
    }

    @Override
    public void render(Matrix4f ms, Vec3 camera, float pt) {
        float width = params.getLineWidth();
        if (width == 0) return;
        params.loadColor(colorTemp);
        renderInner(ms, camera, pt, width, colorTemp, params.disableLineNormals);
    }

    protected void renderInner(Matrix4f ms, Vec3 camera, float pt, float width, Vector4f color, boolean disableNormals) {
        bufferCuboidLine(ms, camera, start, end, width, color, disableNormals);
    }

    public static class EndChasingLineOutline extends LineOutline {
        private float progress = 0;
        private float prevProgress = 0;
        private final boolean lockStart;
        private final Vector3d startTemp = new Vector3d(0, 0, 0);

        public EndChasingLineOutline(boolean lockStart) {
            this.lockStart = lockStart;
        }

        public EndChasingLineOutline setProgress(float progress) {
            prevProgress = this.progress;
            this.progress = progress;
            return this;
        }

        @Override
        protected void renderInner(Matrix4f ms, Vec3 camera, float pt, float width, Vector4f color, boolean disableNormals) {
            float distanceToTarget = prevProgress + (progress - prevProgress) * pt;

            Vector3d end;
            if (lockStart) {
                end = this.start;
            } else {
                end = this.end;
                distanceToTarget = 1 - distanceToTarget;
            }

            Vector3d s = startTemp;
            s.set(
                (this.start.x - end.x) * distanceToTarget + end.x,
                (this.start.y - end.y) * distanceToTarget + end.y,
                (this.start.z - end.z) * distanceToTarget + end.z
            );
            bufferCuboidLine(ms, camera, s, end, width, color, disableNormals);
        }
    }
}
