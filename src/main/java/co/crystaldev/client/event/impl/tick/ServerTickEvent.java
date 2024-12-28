package co.crystaldev.client.event.impl.tick;

import co.crystaldev.client.event.Event;

public class ServerTickEvent extends Event {
  public final int currentTick;
  
  public ServerTickEvent(int currentTick) {
    this.currentTick = currentTick;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\tick\ServerTickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */