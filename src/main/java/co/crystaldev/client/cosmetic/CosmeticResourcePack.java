package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Resources;
import co.crystaldev.client.util.enums.ChatColor;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CosmeticResourcePack extends FolderResourcePack {
  private static final Set<String> DEFAULT_RESOURCE_DOMAINS = (Set<String>)ImmutableSet.of("crystalclient");
  
  public CosmeticResourcePack(File directoryIn) {
    super(directoryIn);
  }
  
  public Set<String> getResourceDomains() {
    return DEFAULT_RESOURCE_DOMAINS;
  }
  
  public <T extends net.minecraft.client.resources.data.IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) {
    try {
      return (T)metadataSerializer.parseMetadataSection(metadataSectionName, PACK_META);
    } catch (Throwable ex) {
      return null;
    } 
  }
  
  public BufferedImage getPackImage() throws IOException {
    return TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(Resources.LOGO_BACKGROUND).getInputStream());
  }
  
  public String getPackName() {
    return ChatColor.translate("&b&lCrystal Client &7| &fCosmetics");
  }
  
  private static final JsonObject PACK_META = new JsonObject();
  
  static {
    PACK_META.addProperty("pack_format", 1);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\CosmeticResourcePack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */