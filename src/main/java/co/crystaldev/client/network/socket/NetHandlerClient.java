package co.crystaldev.client.network.socket;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
//import co.crystaldev.client.network.WebClient;
import io.netty.buffer.ByteBuf;
//import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class NetHandlerClient implements INetHandler {
  private static NetHandlerClient INSTANCE;
  
  public NetHandlerClient() {
    if (INSTANCE != null)
      EventBus.unregister(INSTANCE); 
    INSTANCE = this;
  }
  
  public void sendPacket(Packet packet) {
    System.out.println("TODO IMPLEMENT");
//    try {
      ByteBuf buf = Packet.getPacketBuf(packet);
//      WebClient.getInstance().send(buf.array());
//    } catch (WebsocketNotConnectedException ex) {
//      Client.getInstance().connectToSocket(true);
//    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\NetHandlerClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */