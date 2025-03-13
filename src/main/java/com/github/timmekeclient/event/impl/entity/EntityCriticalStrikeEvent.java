package com.github.timmekeclient.event.impl.entity;

import com.github.timmekeclient.event.Cancellable;
import net.minecraft.entity.Entity;

@Cancellable
public class EntityCriticalStrikeEvent extends EntityEvent {
    public EntityCriticalStrikeEvent(Entity entity) {
        super(entity);
    }
}
