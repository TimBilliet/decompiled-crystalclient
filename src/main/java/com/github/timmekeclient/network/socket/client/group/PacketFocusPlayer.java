package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;
import com.github.timmekeclient.util.UsernameUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.UUID;

public class PacketFocusPlayer extends Packet {
    public UUID playerId;

    public UUID focusedId;

    public PacketFocusPlayer(UUID focusedId) {
        this.focusedId = focusedId;
    }

    public PacketFocusPlayer() {
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("focusedId", this.focusedId.toString());
        out.writeString(Reference.GSON.toJson((JsonElement) obj));
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.playerId = UUID.fromString(obj.get("playerId").getAsString());
        this.focusedId = UUID.fromString(obj.get("focusedId").getAsString());
    }

    public void process(INetHandler handler) {
        if (GroupManager.getSelectedGroup() != null) {
            GroupManager.getSelectedGroup().setFocusedId(this.focusedId);
            String player = UsernameUtils.usernameFromUUID(this.playerId);
            String focused = UsernameUtils.usernameFromUUID(this.focusedId);
            Client.sendMessage("&b" + player + "&f has focused &b" + focused, true);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketFocusPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */