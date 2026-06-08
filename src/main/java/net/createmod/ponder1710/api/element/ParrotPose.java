package net.createmod.ponder1710.api.element;

import net.createmod.metanip.math.AngleHelper;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

// Parrot not in 1.7.10 (added in 1.12) - EntityChicken is visual substitute
public abstract class ParrotPose {

    public abstract void tick(PonderScene scene, EntityChicken entity, Vec3 location);

    public EntityChicken create(PonderLevel world) {
        EntityChicken chicken = new EntityChicken(world);
        chicken.setLocationAndAngles(0, 0, 0, 180, 0);
        return chicken;
    }

    public static class DancePose extends ParrotPose {
        @Override
        public EntityChicken create(PonderLevel world) {
            EntityChicken chicken = super.create(world);
            // Chickens don't dance but we can simulate by rotating
            return chicken;
        }

        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            entity.prevRotationYaw = entity.rotationYaw;
            entity.rotationYaw -= 2;
            entity.wingRotation += 0.5f;
        }
    }

    public static class FlappyPose extends ParrotPose {
        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            double dx = entity.posX - entity.prevPosX;
            double dy = entity.posY - entity.prevPosY;
            double dz = entity.posZ - entity.prevPosZ;
            double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
            entity.onGround = false;
            double phase = Math.min(length * 15, 8);
            float f = (float)((PonderUI.ponderTicks % 100) * phase);
            entity.wingRotation = MathHelper.sin(f) + 1;
            if (length == 0) entity.wingRotation = 0;
        }
    }

    public static abstract class FaceVecPose extends ParrotPose {

        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            Vec3 target = getFacedVec(scene);
            if (target == null) return;

            // Eye position approximate
            Vec3 eye = Vec3.createVectorHelper(
                location.xCoord + entity.posX,
                location.yCoord + entity.posY + entity.getEyeHeight(),
                location.zCoord + entity.posZ
            );

            double dx = target.xCoord - eye.xCoord;
            double dy = target.yCoord - eye.yCoord;
            double dz = target.zCoord - eye.zCoord;
            double d3 = MathHelper.sqrt_double(dx*dx + dz*dz);

            float targetPitch = MathHelper.wrapAngleTo180_float(
                (float)-(Math.atan2(dy, d3) * 180.0 / Math.PI));
            float targetYaw = MathHelper.wrapAngleTo180_float(
                (float)-(Math.atan2(dz, dx) * 180.0 / Math.PI) + 90);

            entity.prevRotationPitch = entity.rotationPitch;
            entity.prevRotationYaw   = entity.rotationYaw;
            entity.rotationPitch = AngleHelper.angleLerp(0.4f, entity.rotationPitch, targetPitch);
            entity.rotationYaw   = AngleHelper.angleLerp(0.4f, entity.rotationYaw,   targetYaw);
        }

        protected abstract Vec3 getFacedVec(PonderScene scene);
    }

    public static class FacePointOfInterestPose extends FaceVecPose {
        @Override
        protected Vec3 getFacedVec(PonderScene scene) {
            return scene.getPointOfInterest();
        }
    }

    public static class FaceCursorPose extends FaceVecPose {
        @Override
        protected Vec3 getFacedVec(PonderScene scene) {
            Minecraft mc = Minecraft.getMinecraft();
            // In 1.7.10 mouse coords are in screen pixels, scaled by guiScale
            double scale  = mc.gameSettings.guiScale == 0 ? 2 : mc.gameSettings.guiScale;
            double mouseX = org.lwjgl.input.Mouse.getX() / scale;
            double mouseY = (mc.displayHeight - org.lwjgl.input.Mouse.getY()) / scale;
            return scene.getTransform().screenToScene(mouseX, mouseY, 300, 0);
        }
    }
}
