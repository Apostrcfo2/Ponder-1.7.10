package net.createmod.ponder1710.foundation.element;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.LerpedFloat;

import net.createmod.ponder1710.api.element.MinecartElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class MinecartElementImpl extends AnimatedSceneElementBase implements MinecartElement {

    private final Vec3 location;
    private final LerpedFloat rotation;
    private final float initialRotation;
    @Nullable
    private EntityMinecart entity;
    private final MinecartConstructor constructor;

    public MinecartElementImpl(Vec3 location, float rotation, MinecartConstructor constructor) {
        initialRotation = rotation;
        this.location = Vec3.createVectorHelper(location.xCoord, location.yCoord + 1/16f, location.zCoord);
        this.constructor = constructor;
        this.rotation = LerpedFloat.angular().startWithValue(rotation);
    }

    @Override
    public void reset(PonderScene scene) {
        super.reset(scene);
        if (entity != null) {
            entity.posX = 0;
            entity.posY = 0;
            entity.posZ = 0;
            entity.prevPosX = 0;
            entity.prevPosY = 0;
            entity.prevPosZ = 0;
            entity.lastTickPosX = 0;
            entity.lastTickPosY = 0;
            entity.lastTickPosZ = 0;
        }
        rotation.startWithValue(initialRotation);
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (entity == null)
            entity = constructor.create(null, 0, 0, 0);

        entity.ticksExisted++;
        entity.onGround = true;
        entity.prevPosX = entity.posX;
        entity.prevPosY = entity.posY;
        entity.prevPosZ = entity.posZ;
        entity.lastTickPosX = entity.posX;
        entity.lastTickPosY = entity.posY;
        entity.lastTickPosZ = entity.posZ;
        rotation.tickChaser();
    }

    @Override
    public void setPositionOffset(Vec3 position, boolean immediate) {
        if (entity == null) return;
        entity.posX = position.xCoord;
        entity.posY = position.yCoord;
        entity.posZ = position.zCoord;
        if (immediate) {
            entity.prevPosX = position.xCoord;
            entity.prevPosY = position.yCoord;
            entity.prevPosZ = position.zCoord;
        }
    }

    @Override
    public void setRotation(float angle, boolean immediate) {
        rotation.chase(angle, 0.4f, LerpedFloat.Chaser.EXP);
        if (immediate) rotation.startWithValue(angle);
    }

    @Override
    public Vec3 getPositionOffset() {
        return entity != null ? Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ) : Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public Vec3 getRotation() {
        return Vec3.createVectorHelper(0, rotation.getValue(), 0);
    }

    @Override
    public void renderLast(PonderLevel world, float fade, float pt) {
        if (entity == null)
            entity = constructor.create(null, 0, 0, 0);

        GL11.glPushMatrix();
        GL11.glTranslated(location.xCoord, location.yCoord, location.zCoord);

        double lerpX = MathHelper.lerp(pt, entity.prevPosX, entity.posX);
        double lerpY = MathHelper.lerp(pt, entity.prevPosY, entity.posY);
        double lerpZ = MathHelper.lerp(pt, entity.prevPosZ, entity.posZ);
        GL11.glTranslated(lerpX, lerpY, lerpZ);

        GL11.glRotatef(rotation.getValue(pt), 0, 1, 0);

        // Render entity using 1.7.10 RenderManager
        net.minecraft.client.renderer.entity.RenderManager.instance.renderEntityWithPosYaw(
            entity, 0, 0, 0, rotation.getValue(pt), pt);

        GL11.glPopMatrix();
    }
}
