package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;

@ModuleInfo(name = "World Name", description = "Displays the name of the world onscreen", category = Category.HUD)
public class WorldName extends HudModuleBackground {
  public String getDisplayText() {
    if (this.mc.theWorld == null)
      return "world"; 
    return Client.getCurrentWorldName();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\WorldName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */