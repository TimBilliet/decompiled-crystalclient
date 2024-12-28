package co.crystaldev.client.event.impl.tick;

import co.crystaldev.client.event.impl.player.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerTickEvent extends PlayerEvent {
  public PlayerTickEvent(EntityPlayer player) {
    super(player);
  }
  
  public static class Pre extends PlayerTickEvent {
    public Pre(EntityPlayer player) {
      super(player);
    }
  }
  
  public static class Post extends PlayerTickEvent {
    public Post(EntityPlayer player) {
      super(player);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\tick\PlayerTickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */