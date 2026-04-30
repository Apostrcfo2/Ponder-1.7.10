package net.createmod.ponder1710.api.element;

// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;

public interface ParrotElement extends AnimatedSceneElement {

    void setPositionOffset(Vec3 position, boolean immediate);

    void setRotation(Vec3 eulers, boolean immediate);

    Vec3 getPositionOffset();

    Vec3 getRotation();

    void setPose(ParrotPose pose);
}
