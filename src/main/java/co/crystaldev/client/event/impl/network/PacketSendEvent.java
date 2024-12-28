package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.network.Packet;

public class PacketSendEvent extends Event {
  public Packet packet;
  
  private PacketSendEvent(Packet packet) {
    this.packet = packet;
  }
  
  @Cancellable
  public static class Pre extends PacketSendEvent {
    public Pre(Packet packet) {
      super(packet);
    }
  }
  
  public static class Post extends PacketSendEvent {
    public Post(Packet packet) {
      super(packet);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\PacketSendEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */