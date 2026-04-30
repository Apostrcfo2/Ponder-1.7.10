package net.createmod.ponder1710.api.element;

// import net.createmod.catnip.data.Pair; // TODO: catnip not available - replaced with custom pair
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;

// import net.minecraft.core.BlockPos; // 1.7.10 uses ChunkCoordinates
// import net.minecraft.world.phys.BlockHitResult; // TODO: not available in 1.7.10
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition; // BlockHitResult equivalent in 1.7.10

public interface WorldSectionElement extends AnimatedSceneElement {

    void mergeOnto(WorldSectionElement other);

    void set(Selection selection);

    void add(Selection toAdd);

    void erase(Selection toErase);

    void setCenterOfRotation(Vec3 center);

    void stabilizeRotation(Vec3 anchor);

    // BlockPos -> using x,y,z ints in 1.7.10
    void selectBlock(int x, int y, int z);

    void resetSelectedBlock();

    void queueRedraw();

    boolean isEmpty();

    void setEmpty();

    void setAnimatedRotation(Vec3 eulerAngles, boolean force);

    Vec3 getAnimatedRotation();

    void setAnimatedOffset(Vec3 offset, boolean force);

    Vec3 getAnimatedOffset();

    // TODO: Pair from catnip not available - using Object[] as temporary replacement
    // Pair<Vec3, BlockHitResult> rayTrace(PonderLevel world, Vec3 source, Vec3 target);
    Object[] rayTrace(PonderLevel world, Vec3 source, Vec3 target);
}
