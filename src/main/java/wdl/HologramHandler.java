package wdl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import wdl.api.ISpecialEntityHandler;
import wdl.api.IWDLModDescripted;

public class HologramHandler implements ISpecialEntityHandler, IWDLModDescripted {
  public boolean isValidEnvironment(String version) {
    return true;
  }
  
  public String getEnvironmentErrorMessage(String version) {
    return null;
  }
  
  public String getDisplayName() {
    return "Hologram support";
  }
  
  public Multimap<String, String> getSpecialEntities() {
    HashMultimap hashMultimap = HashMultimap.create();
    hashMultimap.put("ArmorStand", "Hologram");
    return (Multimap<String, String>)hashMultimap;
  }
  
  public String getSpecialEntityName(Entity entity) {
    if (entity instanceof net.minecraft.entity.item.EntityArmorStand && entity
      .isInvisible() && entity
      .hasCustomName())
//      .hasCustomInventoryName())
      return "Hologram"; 
    return null;
  }
  
  public String getSpecialEntityCategory(String name) {
    if (name.equals("Hologram"))
      return "Other"; 
    return null;
  }
  
  public int getSpecialEntityTrackDistance(String name) {
    return -1;
  }
  
  public String getMainAuthor() {
    return "Pokechu22";
  }
  
  public String[] getAuthors() {
    return null;
  }
  
  public String getURL() {
    return null;
  }
  
  public String getDescription() {
    return "Provides basic support for disabling holograms.";
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\HologramHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */