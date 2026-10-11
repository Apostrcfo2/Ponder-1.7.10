package net.createmod.ponder1710.api.scene;

import java.util.function.Supplier;

import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.api.element.MinecartElement;
import net.createmod.ponder1710.api.element.ParrotElement;
import net.createmod.ponder1710.api.element.ParrotPose;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

// Note: Parrot uses EntityChicken as substitute in 1.7.10 (via ParrotElementImpl)
// BlockPos -> x,y,z ints in 1.7.10
// Direction -> ForgeDirection in 1.7.10
public interface SpecialInstructions {

    ElementLink<ParrotElement> createBirb(Vec3 location, Supplier<? extends ParrotPose> pose);

    void changeBirbPose(ElementLink<ParrotElement> birb, Supplier<? extends ParrotPose> pose);

    void movePointOfInterest(Vec3 location);

    void movePointOfInterest(int x, int y, int z);

    void rotateParrot(ElementLink<ParrotElement> link, double xRotation, double yRotation, double zRotation, int duration);

    void moveParrot(ElementLink<ParrotElement> link, Vec3 offset, int duration);

    ElementLink<MinecartElement> createCart(Vec3 location, float angle, MinecartElement.MinecartConstructor type);

    void rotateCart(ElementLink<MinecartElement> link, float yRotation, int duration);

    void moveCart(ElementLink<MinecartElement> link, Vec3 offset, int duration);

    <T extends AnimatedSceneElement> void hideElement(ElementLink<T> link, ForgeDirection direction);
}
