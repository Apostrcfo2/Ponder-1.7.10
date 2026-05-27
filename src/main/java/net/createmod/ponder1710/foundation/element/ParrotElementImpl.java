package net.createmod.ponder1710.foundation.element;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.createmod.metanip.math.AngleHelper;
import net.createmod.ponder1710.api.element.ParrotElement;
import net.createmod.ponder1710.api.element.ParrotPose;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

// Parrot does not exist in 1.7.10 - EntityChicken used as visual substitute
public class ParrotElementImpl extends AnimatedSceneElementBase implements ParrotElement {

    protected Vec3 location;
    @Nullable
    protected EntityChicken entity;
    protected ParrotPose pose;
    protected Supplier<? extends ParrotPose> initialPose;

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
        if (entity != null) {
            entity.setPosition(0, 0, 0);
            entity.prevPosX = entity.prevPosY = entity.prevPosZ = 0;
            entity.prevRotationYaw = entity.rotationYaw = 180;
            entity.prevRotationPitch = entity.rotationPitch = 0;
        }
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (entity == null) {
            entity = pose.create(scene.getWorld());
            entity.rotationYaw = entity.prevRotationYaw = 180;
        }

        entity.ticksExisted++;
        entity.prevRotationYaw  = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
        entity.prevPosX = entity.posX;
        entity.prevPosY = entity.posY;
        entity.prevPosZ = entity.posZ;
        entity.onGround = true;

        pose.tick(scene, entity, location);
    }

    @Override
    public void setPositionOffset(Vec3 position, boolean immediate) {
        if (entity == null) return;
        entity.setPosition(position.xCoord, position.yCoord, position.zCoord);
        if (immediate) {
            entity.prevPosX = position.xCoord;
            entity.prevPosY = position.yCoord;
            entity.prevPosZ = position.zCoord;
        }
    }

    @Override
    public void setRotation(Vec3 eulers, boolean immediate) {
        if (entity == null) return;
        entity.rotationPitch = (float) eulers.xCoord;
        entity.rotationYaw   = (float) eulers.yCoord;
        if (immediate) {
            entity.prevRotationPitch = entity.rotationPitch;
            entity.prevRotationYaw   = entity.rotationYaw;
        }
    }

    @Override
    public Vec3 getPositionOffset() {
        return entity != null
            ? Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ)
            : Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public Vec3 getRotation() {
        return entity != null
            ? Vec3.createVectorHelper(entity.rotationPitch, entity.rotationYaw, 0)
            : Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    protected void renderLast(PonderLevel world, float fade, float pt) {
        if (entity == null) {
            entity = pose.create(world);
            entity.rotationYaw = entity.prevRotationYaw = 180;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(
            (float)(location.xCoord + MathHelper.lerp(pt, entity.prevPosX, entity.posX)),
            (float)(location.yCoord + MathHelper.lerp(pt, entity.prevPosY, entity.posY)),
            (float)(location.zCoord + MathHelper.lerp(pt, entity.prevPosZ, entity.posZ))
        );

        float angle = AngleHelper.angleLerp(pt, entity.prevRotationYaw, entity.rotationYaw);
        GL11.glRotatef(angle, 0, 1, 0);

        RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, angle, pt);

        GL11.glPopMatrix();
    }

    @Override
    public void setPose(ParrotPose pose) {
        this.pose = pose;
    }
}
