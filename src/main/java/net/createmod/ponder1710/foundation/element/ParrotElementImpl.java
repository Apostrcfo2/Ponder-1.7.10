package net.createmod.ponder1710.foundation.element;

import java.util.function.Supplier;

import javax.annotation.Nullable;

// TODO: Parrot entity does not exist in 1.7.10
// This entire class depends on Parrot which was added in 1.12
// Keeping structure but commenting out Parrot-specific code

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10
// import com.mojang.math.Axis; // not available in 1.7.10
// import net.createmod.metanip.math.AngleHelper; // TODO: catnip not available
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10
// import net.minecraft.client.renderer.entity.EntityRenderDispatcher; // different in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.entity.animal.Parrot; // not in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

import net.createmod.ponder1710.api.element.ParrotElement;
import net.createmod.ponder1710.api.element.ParrotPose;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.util.Vec3;

public class ParrotElementImpl extends AnimatedSceneElementBase implements ParrotElement {

    protected Vec3 location;
    protected ParrotPose pose;
    protected Supplier<? extends ParrotPose> initialPose;

    // TODO: Parrot not in 1.7.10
    // @Nullable protected Parrot entity;

    public static ParrotElement create(Vec3 location, Supplier<? extends ParrotPose> pose) {
        return new ParrotElementImpl(location, pose);
    }

    protected ParrotElementImpl(Vec3 location, Supplier<? extends ParrotPose> pose) {
        this.location = location;
        initialPose = pose;
        this.pose = initialPose.get();
    }

    @Override
    public void reset(PonderScene scene) {
        super.reset(scene);
        setPose(initialPose.get());
        // TODO: entity reset - Parrot not in 1.7.10
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        // TODO: Parrot tick - Parrot not in 1.7.10
        // pose.tick(scene, entity, location);
    }

    @Override
    public void setPositionOffset(Vec3 position, boolean immediate) {
        // TODO: Parrot not in 1.7.10
    }

    @Override
    public void setRotation(Vec3 eulers, boolean immediate) {
        // TODO: Parrot not in 1.7.10
    }

    @Override
    public Vec3 getPositionOffset() {
        return Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public Vec3 getRotation() {
        return Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public void setPose(ParrotPose pose) {
        this.pose = pose;
    }

    @Override
    protected void renderLast(PonderLevel world, float fade, float pt) {
        // TODO: Parrot rendering not in 1.7.10
    }
}
