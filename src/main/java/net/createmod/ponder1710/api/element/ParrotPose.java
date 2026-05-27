package net.createmod.ponder1710.api.element;

import net.createmod.metanip.math.AngleHelper;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

// Parrot does not exist in 1.7.10 (added in 1.12)
// EntityChicken is used as visual substitute
public abstract class ParrotPose {

    public abstract void tick(PonderScene scene, EntityChicken entity, Vec3 location);

    public EntityChicken create(PonderLevel world) {
        EntityChicken chicken = new EntityChicken(world);
        chicken.setLocationAndAngles(0, 0, 0, 180, 0);
        return chicken;
    }

    public static class DancePose extends ParrotPose {
        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            entity.setPosition(location.xCoord, location.yCoord, location.zCoord);
            // Simulate dancing by bobbing
            entity.wingRotation += 0.5f;
        }
    }

    public static class FlappyPose extends ParrotPose {
        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            entity.setPosition(location.xCoord, location.yCoord, location.zCoord);
            entity.wingRotation += 0.3f;
        }
    }

    public static abstract class FaceVecPose extends ParrotPose {
        protected abstract Vec3 getTarget(PonderScene scene);

        @Override
        public void tick(PonderScene scene, EntityChicken entity, Vec3 location) {
            entity.setPosition(location.xCoord, location.yCoord, location.zCoord);
            Vec3 target = getTarget(scene);
            if (target == null) return;

            double dx = target.xCoord - location.xCoord;
            double dz = target.zCoord - location.zCoord;
            float yaw = (float)(MathHelper.atan2(dz, dx) * 180 / Math.PI) - 90;
            entity.prevRotationYaw = entity.rotationYaw;
            entity.rotationYaw = AngleHelper.angleLerp(0.4f, entity.rotationYaw, yaw);
        }
    }

    public static class FacePointOfInterestPose extends FaceVecPose {
        @Override
        protected Vec3 getTarget(PonderScene scene) {
            return scene.getPointOfInterest();
        }
    }

    public static class FaceCursorPose extends FaceVecPose {
        @Override
        protected Vec3 getTarget(PonderScene scene) {
            // Use mouse position projected into scene
            Minecraft mc = Minecraft.getMinecraft();
            int mx = mc.currentScreen != null ? mc.mouseHelper.mouseX() : mc.displayWidth / 2;
            int my = mc.currentScreen != null ? mc.mouseHelper.mouseY() : mc.displayHeight / 2;
            return scene.getTransform().screenToScene(mx, my, 100, 0);
        }
    }
}
