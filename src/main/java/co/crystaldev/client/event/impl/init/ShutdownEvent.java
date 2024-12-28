package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.event.Event;
import net.minecraft.client.Minecraft;

public class ShutdownEvent extends Event {
  private final Minecraft mc = Minecraft.getMinecraft();
  
  public Minecraft getMc() {
    return this.mc;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\ShutdownEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */