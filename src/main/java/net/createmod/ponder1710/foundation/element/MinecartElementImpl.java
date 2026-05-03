package net.createmod.ponder1710.foundation.element;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import com.mojang.math.Axis; // not available in 1.7.10
// import net.createmod.catnip.animation.LerpedFloat; // TODO: catnip not available
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10
// import net.minecraft.client.renderer.entity.EntityRenderDispatcher; // different in 1.7.10
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.entity.vehicle.AbstractMinecart; // different package in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10

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
    // TODO: LerpedFloat from catnip not available - replaced with float
    private float rotationValue;
    private float prevRotationValue;
    private final float initialRotation;
    @Nullable
    private EntityMinecart entity;
    private final MinecartConstructor constructor;

    public MinecartElementImpl(Vec3 location, float rotation, MinecartConstructor constructor) {
        initialRotation = rotation;
        this.location = Vec3.createVectorHelper(location.xCoord, location.yCoord + 1/16f, location.zCoord);
        this.constructor = constructor;
        this.rotationValue = rotation;
        this.prevRotationValue = rotation;
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
        rotationValue = initialRotation;
        prevRotationValue = initialRotation;
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
        prevRotationValue = rotationValue;
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
        rotationValue = angle;
        if (immediate)
            prevRotationValue = angle;
    }

    @Override
    public Vec3 getPositionOffset() {
        return entity != null ? Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ) : Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public Vec3 getRotation() {
        return Vec3.createVectorHelper(0, rotationValue, 0);
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

        float lerpRot = MathHelper.lerp(pt, prevRotationValue, rotationValue);
        GL11.glRotatef(lerpRot, 0, 1, 0);

        // TODO: render entity using 1.7.10 RenderManager
        // Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0, 0, 0, 0, pt, false);

        GL11.glPopMatrix();
    }
}
