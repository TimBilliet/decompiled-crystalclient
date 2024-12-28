package co.crystaldev.client.event.data;

import co.crystaldev.client.event.Event;

import java.util.function.Consumer;

public class LambdaEventData extends AbstractEventData {
  public final Consumer<Event> consumer;
  
  public LambdaEventData(Object source, Consumer<Event> consumer, byte priority) {
    super(source, priority);
    this.consumer = consumer;
  }
  
  public void call(Object source, Event event) {
    if (this.consumer != null)
      this.consumer.accept(event); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\data\LambdaEventData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */