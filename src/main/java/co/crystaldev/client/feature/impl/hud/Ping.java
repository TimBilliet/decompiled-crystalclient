package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.network.NetworkPlayerInfo;

@ConfigurableSize
@ModuleInfo(name = "Ping", description = "View your connection latency", category = Category.HUD)
public class Ping extends HudModuleBackground {
  public Tuple<String, String> getInfoHud() {
    return new Tuple("Ping", getPing() + "ms");
  }
  
  public String getDisplayText() {
    return getPing() + "ms";
  }
  
  public long getPing() {
    long ping = 0L;
    NetworkPlayerInfo playerInfo = this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID());
    if (playerInfo != null)
      ping = playerInfo.getResponseTime(); 
    return ping;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\Ping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */