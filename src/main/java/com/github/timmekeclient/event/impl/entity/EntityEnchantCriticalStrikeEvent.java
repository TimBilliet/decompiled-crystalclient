package com.github.timmekeclient.event.impl.entity;

import com.github.timmekeclient.event.Cancellable;
import net.minecraft.entity.Entity;

@Cancellable
public class EntityEnchantCriticalStrikeEvent extends EntityEvent {
    public EntityEnchantCriticalStrikeEvent(Entity entity) {
        super(entity);
    }
}
