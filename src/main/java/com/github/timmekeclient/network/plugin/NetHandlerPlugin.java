package com.github.timmekeclient.network.plugin;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.feature.impl.hud.Cooldowns;
import com.github.timmekeclient.handler.NotificationHandler;
import com.github.timmekeclient.handler.WaypointHandler;
import com.github.timmekeclient.network.Packet;
import com.github.timmekeclient.network.ReadOnly;
import com.github.timmekeclient.network.WriteOnly;
import com.github.timmekeclient.network.plugin.impl.ClientApiHandler;
import com.github.timmekeclient.network.plugin.server.PacketCooldown;
import com.github.timmekeclient.network.plugin.server.PacketNotification;
import com.github.timmekeclient.network.plugin.server.PacketUpdateWorld;
import com.github.timmekeclient.network.plugin.shared.PacketWaypointAdd;
import com.github.timmekeclient.network.plugin.shared.PacketWaypointRemove;
import com.github.timmekeclient.util.objects.Waypoint;

public class NetHandlerPlugin implements INetHandlerPlugin {
    private final ClientApiHandler apiHandler;

    public NetHandlerPlugin(ClientApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    public void sendPacket(Packet packet) {
        if (packet.getClass().isAnnotationPresent(ReadOnly.class))
            throw new RuntimeException("Packet is read-only.");
        this.apiHandler.sendMessage(Packet.getPacketBuf(packet));
    }

    public void handlePacket(byte[] data) {
        Packet packet = Packet.handle(data);
        if (packet instanceof PluginChannelPacket && !packet.getClass().isAnnotationPresent(WriteOnly.class))
            packet.process(this);
    }

    public void handleCooldown(PacketCooldown packetIn) {
        if ((Cooldowns.getInstance()).enabled)
            Cooldowns.getInstance().addCooldown(packetIn.getItemStack(), packetIn.getDuration());
    }

    public void handleUpdateWorld(PacketUpdateWorld packetIn) {
        Client.setCurrentWorld(packetIn.getWorld());
    }

    public void handleNotification(PacketNotification packetIn) {
        if (!packetIn.getTitle().isEmpty()) {
            NotificationHandler.addNotification(packetIn.getTitle(), packetIn.getContent());
        } else {
            NotificationHandler.addNotification(packetIn.getContent());
        }
    }

    public void handleAddWaypoint(PacketWaypointAdd packetIn) {
        WaypointHandler.getInstance().addWaypoint(packetIn.getWaypoint());
    }

    public void handleRemoveWaypoint(PacketWaypointRemove packetIn) {
        Waypoint waypoint = packetIn.getWaypoint();
        WaypointHandler.getInstance().removeWaypointIf(wp ->
                (wp.isServerSided() && wp.getPos().equals(waypoint.getPos()) && wp.getName().equals(waypoint.getName())));
    }
}
