package co.crystaldev.client.network.plugin;

import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.plugin.server.PacketCooldown;
import co.crystaldev.client.network.plugin.server.PacketDisallowedModules;
import co.crystaldev.client.network.plugin.server.PacketNotification;
import co.crystaldev.client.network.plugin.server.PacketUpdateWorld;
import co.crystaldev.client.network.plugin.shared.PacketWaypointAdd;
import co.crystaldev.client.network.plugin.shared.PacketWaypointRemove;

public interface INetHandlerPlugin extends INetHandler {
  void handleDisallowedFeatures(PacketDisallowedModules paramPacketDisallowedModules);

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