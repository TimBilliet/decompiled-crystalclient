package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketGroupPermissionUpdate extends Packet {
    private Rank rank;

    private int permission;

    public PacketGroupPermissionUpdate() {
    }

    public PacketGroupPermissionUpdate(Rank rank, int permission) {
        this.rank = rank;
        this.permission = permission;
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("rank", this.rank.toString());
        obj.addProperty("permission", Integer.valueOf(this.permission));
        out.writeString(Reference.GSON.toJson((JsonElement) obj));
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.rank = Rank.valueOf(obj.get("rank").getAsString());
        this.permission = obj.get("permission").getAsInt();
    }

    public void process(INetHandler handler) {
        Group group = GroupManager.getSelectedGroup();
        if (group != null)
            group.setPermission(this.rank, this.permission);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupPermissionUpdate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */