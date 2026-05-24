package net.createmod.ponder1710.foundation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

// import net.createmod.metanip.outliner.Outline.OutlineParams; // TODO: catnip not available
// import net.createmod.metanip.outliner.Outliner; // TODO: catnip not available
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.world.level.levelgen.structure.BoundingBox; // not available in 1.7.10
// import net.minecraft.world.phys.AABB; // AxisAlignedBB in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.api.scene.Selection;
import net.minecraft.util.Vec3;

public class SelectionImpl {

    // int[] {minX, minY, minZ, maxX, maxY, maxZ} as BoundingBox replacement
    public static Selection of(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new Simple(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static class Compound implements Selection {
        // int[] {x,y,z} as BlockPos replacement
        private final Set<Long> posSet;
        @Nullable
        private Vec3 center;

        public Compound(Simple initial) {
            posSet = new HashSet<>();
            add(initial);
        }

        private Compound(Set<Long> template) {
            posSet = new HashSet<>(template);
        }

        private static long encodePos(int x, int y, int z) {
            return ((long)(x + 30000000)) | ((long)(y + 30000000) << 20) | ((long)(z + 30000000) << 40);
        }

        @Override
        public boolean test(int[] pos) {
            return posSet.contains(encodePos(pos[0], pos[1], pos[2]));
        }

        @Override
        public Selection add(Selection other) {
            other.forEach(p -> posSet.add(encodePos(p[0], p[1], p[2])));
            center = null;
            return this;
        }

        @Override
        public Selection substract(Selection other) {
            other.forEach(p -> posSet.remove(encodePos(p[0], p[1], p[2])));
            center = null;
            return this;
        }

        @Override
        public Vec3 getCenter() {
            if (center != null) return center;
            if (posSet.isEmpty()) return Vec3.createVectorHelper(0, 0, 0);
            double sx = 0, sy = 0, sz = 0;
            for (long encoded : posSet) {
                int x = (int)((encoded & 0xFFFFF) - 30000000);
                int y = (int)(((encoded >> 20) & 0xFFFFF) - 30000000);
                int z = (int)(((encoded >> 40) & 0xFFFFF) - 30000000);
                sx += x; sy += y; sz += z;
            }
            double n = posSet.size();
            return center = Vec3.createVectorHelper(sx/n + 0.5, sy/n + 0.5, sz/n + 0.5);
        }

        @Override
        public Selection copy() {
            return new Compound(posSet);
        }

        @Override
        public Iterator<int[]> iterator() {
            List<int[]> list = new ArrayList<>();
            for (long encoded : posSet) {
                int x = (int)((encoded & 0xFFFFF) - 30000000);
                int y = (int)(((encoded >> 20) & 0xFFFFF) - 30000000);
                int z = (int)(((encoded >> 40) & 0xFFFFF) - 30000000);
                list.add(new int[]{x, y, z});
            }
            return list.iterator();
        }

        @Override
        public void forEach(Consumer<int[]> consumer) {
            for (int[] pos : this) consumer.accept(pos);
        }
    }

    private static class Simple implements Selection {
        private final int minX, minY, minZ, maxX, maxY, maxZ;

        public Simple(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.minX = Math.min(minX, maxX);
            this.minY = Math.min(minY, maxY);
            this.minZ = Math.min(minZ, maxZ);
            this.maxX = Math.max(minX, maxX);
            this.maxY = Math.max(minY, maxY);
            this.maxZ = Math.max(minZ, maxZ);
        }

        @Override
        public boolean test(int[] pos) {
            return pos[0] >= minX && pos[0] <= maxX
                && pos[1] >= minY && pos[1] <= maxY
                && pos[2] >= minZ && pos[2] <= maxZ;
        }

        @Override
        public Selection add(Selection other) {
            return new Compound(this).add(other);
        }

        @Override
        public Selection substract(Selection other) {
            return new Compound(this).substract(other);
        }

        @Override
        public Vec3 getCenter() {
            return Vec3.createVectorHelper(
                (minX + maxX) / 2.0 + 0.5,
                (minY + maxY) / 2.0 + 0.5,
                (minZ + maxZ) / 2.0 + 0.5
            );
        }

        @Override
        public Selection copy() {
            return new Simple(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Override
        public Iterator<int[]> iterator() {
            List<int[]> list = new ArrayList<>();
            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    for (int z = minZ; z <= maxZ; z++)
                        list.add(new int[]{x, y, z});
            return list.iterator();
        }

        @Override
        public void forEach(Consumer<int[]> consumer) {
            for (int[] pos : this) consumer.accept(pos);
        }
    }
}
