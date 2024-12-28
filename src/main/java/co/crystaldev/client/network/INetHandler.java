package co.crystaldev.client.network;

public interface INetHandler {
  void sendPacket(Packet paramPacket);

  default void handlePacket(byte[] data) {
    Packet packet = Packet.handle(data);
    if (packet != null)
      packet.process(this);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\INetHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */