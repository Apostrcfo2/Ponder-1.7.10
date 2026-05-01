package net.createmod.ponder1710.api.scene;

import java.util.function.Supplier;

import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.MinecartElement;
import net.createmod.ponder1710.api.element.ParrotElement;
import net.createmod.ponder1710.api.element.ParrotPose;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // 1.7.10 uses ForgeDirection
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public interface SpecialInstructions {

    // TODO: ParrotElement - Parrot not in 1.7.10
    // ElementLink<ParrotElement> createBirb(Vec3 location, Supplier<? extends ParrotPose> pose);
    // void changeBirbPose(ElementLink<ParrotElement> birb, Supplier<? extends ParrotPose> pose);
    // void rotateParrot(ElementLink<ParrotElement> link, double xRotation, double yRotation, double zRotation, int duration);
    // void moveParrot(ElementLink<ParrotElement> link, Vec3 offset, int duration);

    void movePointOfInterest(Vec3 location);

    // movePointOfInterest(BlockPos) -> movePointOfInterest(int, int, int) in 1.7.10
    void movePointOfInterest(int x, int y, int z);

    ElementLink<MinecartElement> createCart(Vec3 location, float angle, MinecartElement.MinecartConstructor type);

    void rotateCart(ElementLink<MinecartElement> link, float yRotation, int duration);

    void moveCart(ElementLink<MinecartElement> link, Vec3 offset, int duration);

    // Direction -> ForgeDirection in 1.7.10
    <T extends AnimatedSceneElement> void hideElement(ElementLink<T> link, ForgeDirection direction);
}
