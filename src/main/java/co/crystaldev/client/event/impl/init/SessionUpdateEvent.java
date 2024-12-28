package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.event.Event;
import net.minecraft.util.Session;

public class SessionUpdateEvent extends Event {
  private final Session session;
  
  public SessionUpdateEvent(Session session) {
    this.session = session;
  }
  
  public Session getSession() {
    return this.session;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\SessionUpdateEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */