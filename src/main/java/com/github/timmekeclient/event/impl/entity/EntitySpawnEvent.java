package com.github.timmekeclient.event.impl.entity;

import com.github.timmekeclient.event.Cancellable;
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
