package net.createmod.ponder1710.foundation.element;

import net.createmod.ponder1710.api.element.EntityElement;

// import net.minecraft.world.entity.Entity; // different package in 1.7.10
import net.minecraft.entity.Entity;

public class EntityElementImpl extends TrackedElementBase<Entity> implements EntityElement {

    public EntityElementImpl(Entity wrapped) {
        super(wrapped);
    }

    @Override
    public boolean isStillValid(Entity element) {
        return !element.isDead;
    }
}
