package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.io.IOException;
import java.util.UUID;

public class PacketPingLocation extends Packet {
    private String uuid;

    private String username;

    private int x;

    private int y;

    private int z;

    public String getUsername() {
        return this.username;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public PacketPingLocation() {
    }

    public PacketPingLocation(int x, int y, int z) {
        this.uuid = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
        this.username = Minecraft.getMinecraft().getSession().getUsername();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("uuid", this.uuid);
        obj.addProperty("username", this.username);
        obj.addProperty("x", Integer.valueOf(this.x));
        obj.addProperty("y", Integer.valueOf(this.y));
        obj.addProperty("z", Integer.valueOf(this.z));
        out.writeString(Reference.GSON.toJson((JsonElement) obj));
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.uuid = obj.get("uuid").getAsString();
        this.username = obj.get("username").getAsString();
        this.x = obj.get("x").getAsInt();
        this.y = obj.get("y").getAsInt();
        this.z = obj.get("z").getAsInt();
    }

    public void process(INetHandler handler) {
        if ((Minecraft.getMinecraft()).theWorld != null) {
            Client.sendMessage("&b" + this.username + "&f has pinged their location at &b" + this.x + " " + this.y + " " + this.z, true);
      if (GroupManager.getSelectedGroup() != null)
        GroupManager.getSelectedGroup().getMember(UUID.fromString(this.uuid)).setPingLocation(new BlockPos(this.x, this.y, this.z));
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketPingLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */