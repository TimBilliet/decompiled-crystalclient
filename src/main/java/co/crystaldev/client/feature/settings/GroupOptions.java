package co.crystaldev.client.feature.settings;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import net.minecraft.client.settings.KeyBinding;

public class GroupOptions {
  @Toggle(label = "Group Patchcrumb")
  public boolean sharedCrumb = true;
  
  @Toggle(label = "Shared Adjusts")
  public boolean sharedAdjusts = true;
  
  @Toggle(label = "Show Members on Map")
  public boolean showMembersOnMap = true;
  
  @Toggle(label = "Only Show Members on Fullscreen Map")
  public boolean onlyShowOnBigMap = false;
  
  @Toggle(label = "Group Chat")
  public boolean groupChat = false;
  
  @Keybind(label = "Group Chat Toggle")
  public KeyBinding groupChatToggle = new KeyBinding("crystalclient.key.toggle_group_chat", 0, "Crystal Client");
  
  @Keybind(label = "Ping Location")
  public KeyBinding pingLocation = new KeyBinding("crystalclient.key.ping_group_location", 0, "Crystal Client");
  
  private static GroupOptions INSTANCE;
  
  public GroupOptions() {
    INSTANCE = this;
    Client.registerKeyBinding(this.groupChatToggle);
    Client.registerKeyBinding(this.pingLocation);
  }
  
  public static GroupOptions getInstance() {
    return (INSTANCE == null) ? (INSTANCE = new GroupOptions()) : INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\settings\GroupOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */