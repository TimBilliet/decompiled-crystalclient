package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.util.ColorObject;

@ModuleInfo(name = "Tab Editor", description = "Modify the default Minecraft Player overlay", category = Category.ALL)
public class TabEditor extends Module {
  @Toggle(label = "Sort by Players on Crystal Client")
  public boolean sortByCrystalPlayers = true;
  
  @Toggle(label = "Show Ping as Number")
  public boolean showPingAsNumber = false;
  
  @Toggle(label = "Highlight Group Members")
  public boolean highlightGroupMembers = false;
  
  @Toggle(label = "Highlight Self")
  public boolean highlightSelf = false;
  
  @PageBreak(label = "Highlight Colors")
  @Colour(label = "Highlight Color (Self)")
  public ColorObject selfHighlightColor = new ColorObject(70, 255, 100, 70);
  
  @Colour(label = "Highlight Color (Group Member)")
  public ColorObject groupHighlightColor = new ColorObject(70, 230, 255, 70);
  
  private static TabEditor INSTANCE;
  
  public TabEditor() {
    this.enabled = true;
    INSTANCE = this;
  }
  
  public static TabEditor getInstance() {
    return INSTANCE;
  }
}

