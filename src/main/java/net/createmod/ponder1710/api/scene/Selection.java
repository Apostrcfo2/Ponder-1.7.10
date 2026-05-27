package net.createmod.ponder1710.api.scene;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.createmod.metanip.outliner.Outline;
import net.createmod.metanip.outliner.Outliner;
import net.minecraft.util.Vec3;

// Selection uses int[] {x,y,z} instead of BlockPos in 1.7.10
public interface Selection extends Iterable<int[]>, Predicate<int[]> {

    Selection add(Selection other);
    Selection substract(Selection other);
    Selection copy();
    Vec3 getCenter();

    Outline.OutlineParams makeOutline(Outliner outliner, Object slot);

    default Outline.OutlineParams makeOutline(Outliner outliner) {
        return makeOutline(outliner, this);
    }

    void forEach(Consumer<int[]> consumer);

    // Convenience: iterate with separate x,y,z
    default void forEach(TriConsumer consumer) {
        forEach(pos -> consumer.accept(pos[0], pos[1], pos[2]));
    }

    @FunctionalInterface
    interface TriConsumer {
        void accept(int x, int y, int z);
    }
}
