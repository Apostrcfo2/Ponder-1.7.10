package net.createmod.ponder1710.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

// In 1.7.10 setWorld is package-private - mixin invoker to make it accessible
@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("setWorld")
    void catnip$callSetLevel(World world);
}
