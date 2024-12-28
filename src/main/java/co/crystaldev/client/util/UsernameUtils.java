package co.crystaldev.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.UUID;

public class UsernameUtils {
  public static String usernameFromUUID(UUID uuid) {
    WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
    Collection<NetworkPlayerInfo> list = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
    for (NetworkPlayerInfo pl : list) {
      if (pl.getGameProfile().getId().equals(uuid))
        return pl.getGameProfile().getName();
    }
    if (worldClient != null)
      for (EntityPlayer player : ((World)worldClient).playerEntities) {
        if (player.getUniqueID().equals(uuid))
//          return player.getCommandSenderName();
            return player.getName();
      }
    return uuid.toString();
  }

  public static boolean isOnline(UUID uuid) {
    WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
    Collection<NetworkPlayerInfo> list = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
    for (NetworkPlayerInfo pl : list) {
      if (pl.getGameProfile().getId().equals(uuid))
        return true;
    }
    if (worldClient != null)
      for (EntityPlayer player : ((World)worldClient).playerEntities) {
        if (player.getUniqueID().equals(uuid))
          return true;
      }
    return false;
  }

  public static UUID usernameToUUID(String ign) {
    if ((Minecraft.getMinecraft()).theWorld != null)
      for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
        if (player.getGameProfile().getName().equalsIgnoreCase(ign))
          return player.getGameProfile().getId();
      }
    return null;
  }
}
