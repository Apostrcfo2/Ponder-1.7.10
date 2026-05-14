package net.createmod.ponder1710.foundation;

// import net.createmod.catnip.math.VecHelper; // TODO: catnip not available
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.core.Vec3i; // not available in 1.7.10
// import net.minecraft.world.level.levelgen.structure.BoundingBox; // not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.api.scene.PositionUtil;
import net.createmod.ponder1710.api.scene.SceneBuildingUtil;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.api.scene.SelectionUtil;
import net.createmod.ponder1710.api.scene.VectorUtil;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class PonderSceneBuildingUtil implements SceneBuildingUtil {

    private final SelectionUtil select;
    private final VectorUtil vector;
    private final PositionUtil grid;

    // BoundingBox -> int[] {minX, minY, minZ, maxX, maxY, maxZ}
    private final int[] sceneBounds;

    PonderSceneBuildingUtil(int[] sceneBounds) {
        this.sceneBounds = sceneBounds;
        this.select = new PonderSelectionUtil();
        this.vector = new PonderVectorUtil();
        this.grid = new PonderPositionUtil();
    }

    @Override
    public SelectionUtil select() { return select; }

    @Override
    public VectorUtil vector() { return vector; }

    @Override
    public PositionUtil grid() { return grid; }

    public class PonderPositionUtil implements PositionUtil {
        @Override
        public int[] at(int x, int y, int z) {
            return new int[]{x, y, z};
        }

        @Override
        public int[] zero() {
            return at(0, 0, 0);
        }
    }

    public class PonderVectorUtil implements VectorUtil {

        @Override
        public Vec3 centerOf(int x, int y, int z) {
            return Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);
        }

        @Override
        public Vec3 topOf(int x, int y, int z) {
            return blockSurface(x, y, z, ForgeDirection.UP);
        }

        @Override
        public Vec3 blockSurface(int x, int y, int z, ForgeDirection face) {
            return blockSurface(x, y, z, face, 0);
        }

        @Override
        public Vec3 blockSurface(int x, int y, int z, ForgeDirection face, float margin) {
            return Vec3.createVectorHelper(
                x + 0.5 + face.offsetX * (0.5 + margin),
                y + 0.5 + face.offsetY * (0.5 + margin),
                z + 0.5 + face.offsetZ * (0.5 + margin)
            );
        }

        @Override
        public Vec3 of(double x, double y, double z) {
            return Vec3.createVectorHelper(x, y, z);
        }
    }

    public class PonderSelectionUtil implements SelectionUtil {

        private int sizeX() { return sceneBounds == null ? 5 : sceneBounds[3] - sceneBounds[0] + 1; }
        private int sizeY() { return sceneBounds == null ? 5 : sceneBounds[4] - sceneBounds[1] + 1; }
        private int sizeZ() { return sceneBounds == null ? 5 : sceneBounds[5] - sceneBounds[2] + 1; }

        @Override
        public Selection everywhere() {
            return SelectionImpl.of(0, 0, 0, sizeX()-1, sizeY()-1, sizeZ()-1);
        }

        @Override
        public Selection position(int x, int y, int z) {
            return SelectionImpl.of(x, y, z, x, y, z);
        }

        @Override
        public Selection fromTo(int x, int y, int z, int x2, int y2, int z2) {
            return SelectionImpl.of(x, y, z, x2, y2, z2);
        }

        @Override
        public Selection column(int x, int z) {
            return SelectionImpl.of(x, 1, z, x, sizeY()-1, z);
        }

        @Override
        public Selection layer(int y) {
            return layers(y, 1);
        }

        @Override
        public Selection layersFrom(int y) {
            return layers(y, sizeY() - y);
        }

        @Override
        public Selection layers(int y, int height) {
            return SelectionImpl.of(0, y, 0, sizeX()-1, y + Math.min(sizeY()-y, height)-1, sizeZ()-1);
        }

        @Override
        public Selection cuboid(int originX, int originY, int originZ, int sizeX, int sizeY, int sizeZ) {
            return SelectionImpl.of(originX, originY, originZ, originX+sizeX, originY+sizeY, originZ+sizeZ);
        }
    }
}
