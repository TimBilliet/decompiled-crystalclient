package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.Entity;

public class EntityAttackEvent extends EntityEvent {
  private final Entity target;
  
  private final double distance;
  
  public Entity getTarget() {
    return this.target;
  }
  
  public double getDistance() {
    return this.distance;
  }
  
  private EntityAttackEvent(Entity entity, Entity target, double distance) {
    super(entity);
    this.target = target;
    this.distance = distance;
  }
  
  @Cancellable
  public static class Pre extends EntityAttackEvent {
    public Pre(Entity entity, Entity target, double distance) {
      super(entity, target, distance);
    }
  }
  
  public static class Post extends EntityAttackEvent {
    public Post(Entity entity, Entity target, double distance) {
      super(entity, target, distance);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\entity\EntityAttackEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */