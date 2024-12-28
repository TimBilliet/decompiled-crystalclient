package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class WindowResizeEvent extends Event {
  public final int width;
  
  public final int height;
  
  public final ScaledResolution sr;
  
  public WindowResizeEvent(int width, int height, ScaledResolution sr) {
    this.width = width;
    this.height = height;
    this.sr = sr;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\render\WindowResizeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */