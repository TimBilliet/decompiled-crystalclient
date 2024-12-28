package co.crystaldev.client.cache;

import co.crystaldev.client.util.objects.resources.CachedSkin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SkinCache {
  private static SkinCache INSTANCE = null;
  
  private final List<CachedSkin> cachedSkins = new ArrayList<>();
  
  public CachedSkin getCachedSkin(UUID uuid) {
    for (CachedSkin cachedSkin : this.cachedSkins) {
      if (cachedSkin.getUuid().equals(uuid))
        return cachedSkin; 
    } 
    CachedSkin skin = new CachedSkin(uuid);
    this.cachedSkins.add(skin);
    return skin;
  }
  
  public void deleteSkin(UUID uuid) {
    Iterator<CachedSkin> iterator = this.cachedSkins.iterator();
    while (iterator.hasNext()) {
      if (((CachedSkin)iterator.next()).getUuid().equals(uuid)) {
        iterator.remove();
        break;
      } 
    } 
  }
  
  public void add(CachedSkin skin) {
    this.cachedSkins.add(skin);
  }
  
  public static SkinCache getInstance() {
    return (INSTANCE == null) ? (INSTANCE = new SkinCache()) : INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cache\SkinCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */