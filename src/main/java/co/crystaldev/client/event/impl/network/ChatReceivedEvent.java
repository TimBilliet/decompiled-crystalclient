package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.util.IChatComponent;

@Cancellable
public class ChatReceivedEvent extends Event {
  public IChatComponent message;
  
  private byte type;
  
  public void setType(byte type) {
    this.type = type;
  }
  
  public byte getType() {
    return this.type;
  }
  
  public ChatReceivedEvent(IChatComponent message, byte type) {
    this.message = message;
    this.type = type;
  }
  
  public static final class Type {
    public static final byte STANDARD = 0;
    
    public static final byte SYSTEM = 1;
    
    public static final byte STATUS = 2;
    
    public static final byte[] TYPES = new byte[] { 0, 1, 2 };
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\ChatReceivedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */