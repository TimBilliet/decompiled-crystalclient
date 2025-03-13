package com.github.timmekeclient.network.socket.client.cosmetic;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.cosmetic.CosmeticType;
import com.github.timmekeclient.cosmetic.base.Cosmetic;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketUpdateSelectedCosmetic extends Packet {
    private CosmeticType type;

    private Cosmetic cosmetic;

    public PacketUpdateSelectedCosmetic(CosmeticType type, Cosmetic cosmetic) {
        this.type = type;
        this.cosmetic = cosmetic;
    }

    public PacketUpdateSelectedCosmetic() {
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty(this.type.getInternalName(), (this.cosmetic == null) ? "null" : this.cosmetic.getName());
        out.writeString(Reference.GSON.toJson(obj, JsonObject.class));
    }

    public void read(ByteBufWrapper in) throws IOException {
    }

    public void process(INetHandler handler) {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\cosmetic\PacketUpdateSelectedCosmetic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */