package co.crystaldev.client.patcher.enhancement;

import co.crystaldev.client.patcher.enhancement.text.EnhancedFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

import java.util.HashMap;
import java.util.Map;

public class EnhancementManager {
  private static final EnhancementManager instance = new EnhancementManager();
  
  private final Map<Class<? extends Enhancement>, Enhancement> enhancementMap = new HashMap<>();
  
  public EnhancementManager() {
    IReloadableResourceManager resourceManager = (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
    resourceManager.registerReloadListener(new ReloadListener());
    this.enhancementMap.put(EnhancedFontRenderer.class, new EnhancedFontRenderer());
  }
  
  public void tick() {
    for (Map.Entry<Class<? extends Enhancement>, Enhancement> entry : this.enhancementMap.entrySet())
      ((Enhancement)entry.getValue()).tick(); 
  }
  
  public <T extends Enhancement> T getEnhancement(Class<T> enhancement) {
    return (T)this.enhancementMap.get(enhancement);
  }
  
  public static EnhancementManager getInstance() {
    return instance;
  }
}
