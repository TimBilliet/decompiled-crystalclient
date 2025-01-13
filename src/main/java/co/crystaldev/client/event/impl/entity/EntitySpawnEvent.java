package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.Entity;

public class EntitySpawnEvent extends EntityEvent {
    private EntitySpawnEvent(Entity entity) {
        super(entity);
    }

    @Cancellable
    public static class Pre extends EntitySpawnEvent {
        public Pre(Entity entity) {
            super(entity);
        }
    }

    public static class Post extends EntitySpawnEvent {
        public Post(Entity entity) {
            super(entity);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\entity\EntitySpawnEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */