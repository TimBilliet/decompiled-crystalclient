package com.github.lunatrius.core.handler;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class DelayedGuiDisplayTicker {
  private final GuiScreen guiScreen;
  
  private int ticks;
  
  private DelayedGuiDisplayTicker(GuiScreen guiScreen, int delay) {
    this.guiScreen = guiScreen;
    this.ticks = delay;
  }
  
  @SubscribeEvent
  public void onClientTick(ClientTickEvent.Pre event) {
    this.ticks--;
    if (this.ticks < 0) {
      Minecraft.getMinecraft().displayGuiScreen(this.guiScreen);
      EventBus.unregister(this);
    } 
  }
  
  public static void create(GuiScreen guiScreen, int delay) {
    EventBus.register(new DelayedGuiDisplayTicker(guiScreen, delay));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\handler\DelayedGuiDisplayTicker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */