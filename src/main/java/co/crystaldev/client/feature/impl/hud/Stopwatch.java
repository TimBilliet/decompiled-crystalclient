package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.settings.KeyBinding;

@ConfigurableSize
@ModuleInfo(name = "Stopwatch", description = "Displays a stopwatch onscreen", category = Category.HUD)
public class Stopwatch extends HudModuleBackground implements IRegistrable {
  @Keybind(label = "Start/Stop")
  public KeyBinding keybinding = new KeyBinding("crystalclient.key.start_stop_stopwatch", 0, "Crystal Client - Stopwatch");
  
  @Keybind(label = "Reset")
  public KeyBinding reset = new KeyBinding("crystalclient.key.reset_stopwatch", 0, "Crystal Client - Stopwatch");
  
  private boolean started = false;
  
  private long currentTime = 0L;
  
  private long timeElapsed = 0L;
  
  public Stopwatch() {
    this.enabled = false;
    this.hasInfoHud = true;
    this.width = 60;
    this.height = 18;
    this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 70.0F, 127.0F);
  }
  
  public Tuple<String, String> getInfoHud() {
    return new Tuple("Stopwatch", getDisplayText());
  }
  
  public String getDisplayText() {
    long millis = System.currentTimeMillis();
    if (millis != this.currentTime) {
      if (this.started && this.currentTime != 0L)
        this.timeElapsed += millis - this.currentTime; 
      this.currentTime = millis;
    } 
    if (this.timeElapsed != 0L) {
      int hours = (int)(this.timeElapsed / 3600000L % 24L);
      int minutes = (int)(this.timeElapsed / 60000L % 60L);
      int seconds = (int)(this.timeElapsed / 1000L) % 60;
      return ((hours != 0) ? (hours + ":") : "") + ((minutes < 10) ? ("0" + minutes) : String.valueOf(Integer.valueOf(minutes))) + ":" + ((seconds < 10) ? ("0" + seconds) : String.valueOf(Integer.valueOf(seconds)));
    } 
    return "00:00";
  }
  
  public void registerEvents() {
    EventBus.register(this, InputEvent.Key.class, ev -> {
          if (this.reset.isPressed()) {
            this.started = false;
            this.currentTime = 0L;
            this.timeElapsed = 0L;
          } 
          if (this.keybinding.isPressed())
            this.started = !this.started; 
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\Stopwatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */