package net.createmod.ponder1710.api.scene;

import java.util.function.Consumer;
import java.util.function.Predicate;

// import net.createmod.metanip.outliner.Outline; // TODO: catnip not available
// import net.createmod.metanip.outliner.Outliner; // TODO: catnip not available
// import net.minecraft.core.BlockPos; // 1.7.10 uses ChunkCoordinates or x,y,z
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;

public interface Selection extends Iterable<int[]>, Predicate<int[]> {

    Selection add(Selection other);

    Selection substract(Selection other);

    Selection copy();

    Vec3 getCenter();

    // TODO: makeOutline - Outliner from catnip not available in 1.7.10
    // Outline.OutlineParams makeOutline(Outliner outliner, Object slot);
    // default Outline.OutlineParams makeOutline(Outliner outliner) { ... }

    // Helper to iterate with x,y,z
    void forEach(Consumer<int[]> consumer);
}
