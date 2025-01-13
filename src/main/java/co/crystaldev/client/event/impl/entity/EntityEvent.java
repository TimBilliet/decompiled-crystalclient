package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Event;
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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\entity\EntityEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */