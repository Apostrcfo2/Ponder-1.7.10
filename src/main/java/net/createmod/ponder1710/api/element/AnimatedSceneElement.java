package net.createmod.ponder1710.api.element;

// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;

public interface AnimatedSceneElement extends PonderSceneElement {

    void forceApplyFade(float fade);

    void setFade(float fade);

    void setFadeVec(Vec3 fadeVec);
}
