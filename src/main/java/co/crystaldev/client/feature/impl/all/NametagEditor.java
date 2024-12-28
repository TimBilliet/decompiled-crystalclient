package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

@ModuleInfo(name = "Nametag Editor", description = "Edit entity nameplates", category = Category.ALL)
public class NametagEditor extends Module {
  @Toggle(label = "Show Client Logo")
  public boolean showClientLogo = true;
  
  @Toggle(label = "Text Shadow")
  public boolean textShadow = false;
  
  private static NametagEditor INSTANCE;
  
  public NametagEditor() {
    this.enabled = true;
    this.canBeDisabled = false;
    INSTANCE = this;
  }
  
  public static NametagEditor getInstance() {
    return INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\NametagEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */