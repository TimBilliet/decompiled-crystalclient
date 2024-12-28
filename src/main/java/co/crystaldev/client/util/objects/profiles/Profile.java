package co.crystaldev.client.util.objects.profiles;

import co.crystaldev.client.Config;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.handler.ModuleHandler;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Profile {
  @SerializedName("id")
  private final UUID id;
  
  @SerializedName("last_modified")
  private long lastModified;
  
  @SerializedName("name")
  private String name;
  
  public UUID getId() {
    return this.id;
  }
  
  public long getLastModified() {
    return this.lastModified;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  @SerializedName("auto_use")
  private String autoUseServer = null;
  
  @SerializedName("color")
  private Color color;
  
  @SerializedName("values")
  private List<ModuleConfiguration> values;
  
  public void setAutoUseServer(String autoUseServer) {
    this.autoUseServer = autoUseServer;
  }
  
  public String getAutoUseServer() {
    return this.autoUseServer;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }
  
  public Color getColor() {
    return this.color;
  }
  
  public void setValues(List<ModuleConfiguration> values) {
    this.values = values;
  }
  
  public List<ModuleConfiguration> getValues() {
    return this.values;
  }
  
  public Profile(String name) {
    this(name, new Color(255, 255, 255, 255));
  }
  
  public Profile(String name, Color color) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.lastModified = System.currentTimeMillis();
    this.values = new ArrayList<>();
    saveCurrentModConfiguration();
    this.color = color;
  }
  
  public void saveCurrentModConfiguration() {
    this.lastModified = System.currentTimeMillis();
    this.values.clear();
    for (Module module : ModuleHandler.getModules()) {
      JsonObject obj = Config.getInstance().saveObjectToJson(module);
      if (obj != null)
        this.values.add(new ModuleConfiguration(module.getSanitizedName(), obj)); 
    } 
  }
  
  public void loadCurrentModConfiguration() {
    for (ModuleConfiguration config : this.values) {
      for (Module module : ModuleHandler.getModules()) {
        if (module.getSanitizedName().equals(config.getName()))
          Config.getInstance().loadFromJsonObject(config.getConfig(), module); 
      } 
    } 
  }
  
  public String toString() {
    StringBuilder elements = new StringBuilder(this.values.isEmpty() ? "[]" : "[");
    for (ModuleConfiguration config : this.values)
      elements.append("\n\t\t").append(config.toString()); 
    if (!elements.toString().endsWith("]"))
      elements.append("\n\t").append("]").append('\n'); 
    return String.format("Profile {\n\tid = %s,\n\tname = %s,\n\tlastModified = %d,\n\tautoUse = %s,\n\tmods = %s}", new Object[] { this.id
          .toString(), this.name, Long.valueOf(this.lastModified), (this.autoUseServer == null) ? "null" : this.autoUseServer, elements });
  }
  
  public boolean equals(Object object) {
    if (!(object instanceof Profile))
      return false; 
    return ((Profile)object).getId().equals(this.id);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\profiles\Profile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */