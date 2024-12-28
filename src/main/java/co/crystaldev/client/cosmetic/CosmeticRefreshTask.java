package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Client;
//import co.crystaldev.client.network.Packet;
//import co.crystaldev.client.network.socket.client.cosmetic.PacketRequestCosmetics;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.cosmetic.PacketRequestCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashSet;
import java.util.UUID;

public class CosmeticRefreshTask implements Runnable {
  public void run() {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.theWorld != null) {
      HashSet<UUID> ids = new HashSet<>();
      for (NetworkPlayerInfo player : mc.getNetHandler().getPlayerInfoMap())
        ids.add(player.getGameProfile().getId());
      PacketRequestCosmetics packet = new PacketRequestCosmetics(ids);
      Client.sendPacket(packet);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\CosmeticRefreshTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */