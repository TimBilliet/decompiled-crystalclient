package com.github.timmekeclient.network.plugin;

import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.plugin.server.PacketCooldown;
import com.github.timmekeclient.network.plugin.server.PacketNotification;
import com.github.timmekeclient.network.plugin.server.PacketUpdateWorld;
import com.github.timmekeclient.network.plugin.shared.PacketWaypointAdd;
import com.github.timmekeclient.network.plugin.shared.PacketWaypointRemove;

public interface INetHandlerPlugin extends INetHandler {

    void handleCooldown(PacketCooldown paramPacketCooldown);

    void handleUpdateWorld(PacketUpdateWorld paramPacketUpdateWorld);

    void handleNotification(PacketNotification paramPacketNotification);

    void handleRemoveWaypoint(PacketWaypointRemove paramPacketWaypointRemove);

    void handleAddWaypoint(PacketWaypointAdd paramPacketWaypointAdd);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\INetHandlerPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */