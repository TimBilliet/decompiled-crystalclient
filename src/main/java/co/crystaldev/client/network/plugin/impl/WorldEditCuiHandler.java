package co.crystaldev.client.network.plugin.impl;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.impl.network.WorldEditCuiEvent;
import co.crystaldev.client.network.plugin.MessageHandler;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class WorldEditCuiHandler extends MessageHandler {
  private String event;
  
  public void fromBytes(ByteBuf buf) {
    int readableBytes = buf.readableBytes();
    if (readableBytes > 0) {
      byte[] payload = new byte[readableBytes];
      buf.readBytes(payload);
      this.event = new String(payload, Charsets.UTF_8);
      (new WorldEditCuiEvent(this.event)).call();
    } else {
      Reference.LOGGER.warn("Invalid (zero length) payload received from World Edit");
    } 
  }
  
  public void toBytes(ByteBuf buf) {}
  
  public void onMessage() {
    Reference.LOGGER.info("Received message from World Edit: " + this.event);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\impl\WorldEditCuiHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */