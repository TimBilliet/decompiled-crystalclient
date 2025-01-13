package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.Entity;

@Cancellable
public class EntityCriticalStrikeEvent extends EntityEvent {
    public EntityCriticalStrikeEvent(Entity entity) {
        super(entity);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\entity\EntityCriticalStrikeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */