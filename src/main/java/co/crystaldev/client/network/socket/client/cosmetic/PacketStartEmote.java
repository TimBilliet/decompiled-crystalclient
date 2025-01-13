package co.crystaldev.client.network.socket.client.cosmetic;

import co.crystaldev.client.Client;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
//import mchorse.emoticons.common.EmoteAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.UUID;

public class PacketStartEmote extends Packet {
    private String emoteId;

    private UUID player;

    public PacketStartEmote(String emoteId) {
        this.emoteId = emoteId;
        this.player = Client.getUniqueID();
    }

    public PacketStartEmote() {
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.emoteId);
        out.writeUUID(this.player);
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.player = in.readUUID();
        this.emoteId = in.readString();
    }

    public void process(INetHandler handler) {
        if (this.player != null && this.emoteId != null) {
            WorldClient worldClient = (Minecraft.getMinecraft()).theWorld;
            if (worldClient == null)
                return;
            for (EntityPlayer player : (Minecraft.getMinecraft()).theWorld.playerEntities) {
                if (player.getUniqueID().equals(this.player)) {
//          EmoteAPI.setEmoteClient(this.emoteId, player);
                    break;
                }
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\cosmetic\PacketStartEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */