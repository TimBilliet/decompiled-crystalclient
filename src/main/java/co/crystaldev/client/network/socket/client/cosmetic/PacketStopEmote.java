package co.crystaldev.client.network.socket.client.cosmetic;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
//import mchorse.emoticons.common.EmoteAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.UUID;

public class PacketStopEmote extends Packet {
    private UUID player;

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.player = in.readUUID();
    }

    public void process(INetHandler handler) {
        if (this.player != null) {
            WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
            if (worldClient != null)
                for (EntityPlayer player : ((World) worldClient).playerEntities) {
//          if (player.getUniqueID().equals(this.player))
//            EmoteAPI.setEmoteClient("", player);
                }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\cosmetic\PacketStopEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */