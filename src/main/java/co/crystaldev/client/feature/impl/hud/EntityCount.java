package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.duck.RenderGlobalExt;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;

@ConfigurableSize
@ModuleInfo(name = "Entity Count", description = "Displays the total rendered entity count onscreen", category = Category.HUD)
public class EntityCount extends HudModuleBackground {
  public Tuple<String, String> getInfoHud() {
    return new Tuple("Entities", ((RenderGlobalExt)this.mc.renderGlobal).getHudEntityCount());
  }
  
  public String getDisplayText() {
    return "E: " + ((RenderGlobalExt)this.mc.renderGlobal).getHudEntityCount();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\EntityCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */