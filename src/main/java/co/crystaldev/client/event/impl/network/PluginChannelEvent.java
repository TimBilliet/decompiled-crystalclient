package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Event;
import co.crystaldev.client.network.plugin.MessageHandler;
import net.minecraft.network.PacketBuffer;

public class PluginChannelEvent extends Event {
  public static class Register extends PluginChannelEvent {}

  public static class MessageReceived extends PluginChannelEvent {
    private final MessageHandler handler;

    private final PacketBuffer buffer;

    public MessageReceived(MessageHandler handler, PacketBuffer buffer) {
      this.handler = handler;
      this.buffer = buffer;
    }

    public MessageHandler getHandler() {
      return this.handler;
    }

    public PacketBuffer getBuffer() {
      return this.buffer;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\PluginChannelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */