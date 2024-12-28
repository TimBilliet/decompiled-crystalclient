package co.crystaldev.client.network.plugin;

import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

public abstract class PluginChannelPacket extends Packet {
  public final void process(INetHandler handler) {
    process((NetHandlerPlugin)handler);
  }
  
  public abstract void process(NetHandlerPlugin paramNetHandlerPlugin);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\PluginChannelPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */