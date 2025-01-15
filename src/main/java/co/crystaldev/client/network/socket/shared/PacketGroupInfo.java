package co.crystaldev.client.network.socket.shared;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketGroupInfo extends Packet {
    public JsonArray array;

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.array = (JsonArray) Reference.GSON.fromJson(in.readString(), JsonArray.class);
    }

    public void process(INetHandler handler) {
        GroupManager.getGroups().clear();
        for (JsonElement element : this.array) {
            JsonObject object = element.getAsJsonObject();
            Group group = new Group(object.get("id").getAsString(), object.get("name").getAsString(), null, null, null, null, null, null);
            GroupManager.getGroups().add(group);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\shared\PacketGroupInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */