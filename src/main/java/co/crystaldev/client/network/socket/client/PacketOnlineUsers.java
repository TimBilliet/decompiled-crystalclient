package co.crystaldev.client.network.socket.client;

import co.crystaldev.client.Reference;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.handler.PlayerHandler;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketOnlineUsers extends Packet {
    @SerializedName("uuids")
    private Set<UUID> uuids;

    public PacketOnlineUsers() {
    }

    public PacketOnlineUsers(Set<UUID> uuids) {
        this.uuids = new HashSet<>(uuids);
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(Reference.GSON.toJson(this.uuids));
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.uuids = Reference.GSON.fromJson(in.readString(), (new TypeToken<HashSet<UUID>>() {}).getType());
    }

    public void process(INetHandler handler) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.isSingleplayer() || mc.getNetHandler() == null)
            return;
        PlayerHandler.getInstance().getOnlineUsers().addAll(this.uuids);
        for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (this.uuids.contains(player.getGameProfile().getId()))
                ((NetworkPlayerInfoExt) player).setOnlineStatus(true);
        }
    }
}