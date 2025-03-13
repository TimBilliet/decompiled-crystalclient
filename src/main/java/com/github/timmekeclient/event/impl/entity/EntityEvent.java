package com.github.timmekeclient.event.impl.entity;

import com.github.timmekeclient.event.Event;
import net.minecraft.entity.Entity;

public abstract class EntityEvent extends Event {
    private final Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }
}
