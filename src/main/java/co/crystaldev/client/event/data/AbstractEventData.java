package co.crystaldev.client.event.data;

import co.crystaldev.client.event.Event;

public abstract class AbstractEventData {
  public final Object source;
  
  public final byte priority;
  
  public AbstractEventData(Object source, byte priority) {
    this.source = source;
    this.priority = priority;
  }
  
  public abstract void call(Object paramObject, Event paramEvent);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\data\AbstractEventData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */