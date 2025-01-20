package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.Entity;

@Cancellable
public class EntityCriticalStrikeEvent extends EntityEvent {
    public EntityCriticalStrikeEvent(Entity entity) {
        super(entity);
    }
}
