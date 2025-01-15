package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Client;
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