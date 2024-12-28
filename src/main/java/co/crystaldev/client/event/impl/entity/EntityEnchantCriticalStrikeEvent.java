package co.crystaldev.client.event.impl.entity;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.Entity;

@Cancellable
public class EntityEnchantCriticalStrikeEvent extends EntityEvent {
  public EntityEnchantCriticalStrikeEvent(Entity entity) {
    super(entity);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\entity\EntityEnchantCriticalStrikeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */