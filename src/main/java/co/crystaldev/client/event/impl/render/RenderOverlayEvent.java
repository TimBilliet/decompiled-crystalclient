package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class RenderOverlayEvent extends Event {
  public final float partialTicks;
  
  public RenderOverlayEvent(float partialTicks) {
    this.partialTicks = partialTicks;
  }
  
  public static class All extends RenderOverlayEvent {
    public final ScaledResolution scaledResolution;
    
    public All(float partialTicks, ScaledResolution sr) {
      super(partialTicks);
      this.scaledResolution = sr;
    }
  }
  
  @Cancellable
  public static class BossBar extends RenderOverlayEvent {
    public BossBar(float partialTicks) {
      super(partialTicks);
    }
  }
  
  @Cancellable
  public static class Crosshair extends RenderOverlayEvent {
    private final boolean visible;
    
    public boolean isVisible() {
      System.out.println("crosshairrenderoverlayev");
      return this.visible;
    }
    
    public Crosshair(float partialTicks, boolean visible) {
      super(partialTicks);
      this.visible = visible;
    }
  }
  
  public static class Gui extends RenderOverlayEvent {
    private final GuiScreen screen;
    
    public GuiScreen getScreen() {
      return this.screen;
    }
    
    public Gui(GuiScreen screen, float partialTicks) {
      super(partialTicks);
      this.screen = screen;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\render\RenderOverlayEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */