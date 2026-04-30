package net.createmod.ponder1710.api.element;

// import net.minecraft.world.entity.vehicle.AbstractMinecart; // 1.7.10 uses different package
// import net.minecraft.world.level.Level; // 1.7.10 uses World
// import net.minecraft.world.phys.Vec3; // 1.7.10 uses net.minecraft.util.Vec3
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface MinecartElement extends AnimatedSceneElement {

    void setPositionOffset(Vec3 position, boolean immediate);

    void setRotation(float angle, boolean immediate);

    Vec3 getPositionOffset();

    Vec3 getRotation();

    interface MinecartConstructor {
        // AbstractMinecart -> EntityMinecart in 1.7.10
        // Level -> World in 1.7.10
        EntityMinecart create(World w, double x, double y, double z);
    }
}
