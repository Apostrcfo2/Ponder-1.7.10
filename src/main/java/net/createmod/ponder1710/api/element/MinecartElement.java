package net.createmod.ponder1710.api.element;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

// AbstractMinecart -> EntityMinecart in 1.7.10
// Level -> World in 1.7.10
public interface MinecartElement extends AnimatedSceneElement {

    void setPositionOffset(Vec3 position, boolean immediate);

    void setRotation(float angle, boolean immediate);

    Vec3 getPositionOffset();

    Vec3 getRotation();

    interface MinecartConstructor {
        EntityMinecart create(World w, double x, double y, double z);
    }
}
