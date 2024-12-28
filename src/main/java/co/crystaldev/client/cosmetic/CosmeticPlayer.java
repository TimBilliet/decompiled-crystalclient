package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.util.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.UUID;

public class CosmeticPlayer {
  private final UUID player;
  
  public UUID getPlayer() {
    return this.player;
  }
  
  private Cosmetic cloak = null;
  
  public Cosmetic getCloak() {
    return this.cloak;
  }
  
  public void setCloak(Cosmetic cloak) {
    this.cloak = cloak;
  }
  
  private Cosmetic wings = null;
  
  public Cosmetic getWings() {
    return this.wings;
  }
  
  public void setWings(Cosmetic wings) {
    this.wings = wings;
  }
  
  private Cosmetic color = null;
  
  public Cosmetic getColor() {
    return this.color;
  }
  
  public void setColor(Cosmetic color) {
    this.color = color;
  }
  
  private boolean shouldUpdateCosmetic = true;
  
  public boolean isShouldUpdateCosmetic() {
    return this.shouldUpdateCosmetic;
  }
  
  public void setShouldUpdateCosmetic(boolean shouldUpdateCosmetic) {
    this.shouldUpdateCosmetic = shouldUpdateCosmetic;
  }
  
  private boolean shouldHideLegacyCosmetics = false;
  
  public boolean isShouldHideLegacyCosmetics() {
    return this.shouldHideLegacyCosmetics;
  }
  
  public void setShouldHideLegacyCosmetics(boolean shouldHideLegacyCosmetics) {
    this.shouldHideLegacyCosmetics = shouldHideLegacyCosmetics;
  }
  
  private static final Field locationOfCape = findField(AbstractClientPlayer.class, "locationOfCape");
  
  public CosmeticPlayer(UUID player) {
    this.player = player;
  }
  
  public boolean hasLocationOfCape() {
    return (getLocationOfCape() != null);
  }
  
  public ResourceLocation getLocationOfCape() {
    WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
    EntityPlayer player;
    if (locationOfCape != null && worldClient != null && (player = worldClient.getPlayerEntityByUUID(this.player)) != null)
      try {
        return (ResourceLocation)locationOfCape.get(player);
      } catch (IllegalAccessException ex) {
        Reference.LOGGER.error("Unable to access locationOfCape field", ex);
      }  
    return null;
  }
  
  public void setLocationOfCape(ResourceLocation resourceLocation) {
    WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
    EntityPlayer player;
    if (locationOfCape != null && worldClient != null && (player = worldClient.getPlayerEntityByUUID(this.player)) != null)
      try {
        locationOfCape.set(player, resourceLocation);
      } catch (IllegalAccessException ex) {
        Reference.LOGGER.error("Unable to access locationOfCape field", ex);
      }  
  }
  
  public boolean hasCloak() {
    return (this.cloak != null);
  }
  
  public boolean hasWings() {
    return (this.wings != null);
  }
  
  public boolean hasColor() {
    return (this.color != null);
  }
  
  public boolean shouldUpdateCosmetic() {
    return this.shouldUpdateCosmetic;
  }
  
  private static Field findField(Class<?> clazz, String... fieldNames) {
    try {
      return ReflectionHelper.findField(clazz, fieldNames);
    } catch (ReflectionHelper.UnableToFindFieldException ex) {
      return null;
    } 
  }
}
