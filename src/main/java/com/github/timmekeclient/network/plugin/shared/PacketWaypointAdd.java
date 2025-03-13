package com.github.timmekeclient.network.plugin.shared;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.plugin.NetHandlerPlugin;
import com.github.timmekeclient.network.plugin.PluginChannelPacket;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.objects.Waypoint;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class PacketWaypointAdd extends PluginChannelPacket {
    private Waypoint waypoint;

    public Waypoint getWaypoint() {
        return this.waypoint;
    }

    public PacketWaypointAdd(Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    public PacketWaypointAdd() {
        this(null);
    }

    public void write(ByteBufWrapper out) throws IOException {
        if (this.waypoint == null)
            return;
        out.writeString(this.waypoint.getName());
        out.writeVarInt(this.waypoint.getColor().getRGB());
        out.writeBool(this.waypoint.getColor().isChroma());
        out.writeVarInt(this.waypoint.getPos().getX());
        out.writeVarInt(this.waypoint.getPos().getY());
        out.writeVarInt(this.waypoint.getPos().getZ());
    }

    public void read(ByteBufWrapper in) throws IOException {
        boolean canBeDeleted = in.readBool();
        String name = in.readString();
        ColorObject color = ColorObject.fromColor(in.readVarInt(), in.readBool());
        BlockPos pos = new BlockPos(in.readVarInt(), in.readVarInt(), in.readVarInt());
        this

                .waypoint = (new Waypoint(name, Client.formatConnectedServerIp(), pos, color)).setCanBeDeleted(canBeDeleted).setServerSided(true);
    }

    public void process(NetHandlerPlugin handler) {
        handler.handleAddWaypoint(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\shared\PacketWaypointAdd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */