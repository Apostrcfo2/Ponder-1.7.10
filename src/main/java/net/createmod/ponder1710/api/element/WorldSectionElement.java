package net.createmod.ponder1710.api.element;

import net.createmod.metanip.data.Pair;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public interface WorldSectionElement extends AnimatedSceneElement {

    void mergeOnto(WorldSectionElement other);
    void set(Selection selection);
    void add(Selection toAdd);
    void erase(Selection toErase);
    void setCenterOfRotation(Vec3 center);
    void stabilizeRotation(Vec3 anchor);

    // BlockPos -> x,y,z ints in 1.7.10
    void selectBlock(int x, int y, int z);
    void resetSelectedBlock();
    void queueRedraw();
    boolean isEmpty();
    void setEmpty();
    void setAnimatedRotation(Vec3 eulerAngles, boolean force);
    Vec3 getAnimatedRotation();
    void setAnimatedOffset(Vec3 offset, boolean force);
    Vec3 getAnimatedOffset();

    // Pair<Vec3, MovingObjectPosition> replaces Pair<Vec3, BlockHitResult>
    Pair<Vec3, MovingObjectPosition> rayTrace(PonderLevel world, Vec3 source, Vec3 target);
}
