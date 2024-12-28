package co.crystaldev.client.network.plugin;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.impl.hud.Cooldowns;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.ReadOnly;
import co.crystaldev.client.network.WriteOnly;
import co.crystaldev.client.network.plugin.impl.ClientApiHandler;
import co.crystaldev.client.network.plugin.server.PacketCooldown;
import co.crystaldev.client.network.plugin.server.PacketDisallowedModules;
import co.crystaldev.client.network.plugin.server.PacketNotification;
import co.crystaldev.client.network.plugin.server.PacketUpdateWorld;
import co.crystaldev.client.network.plugin.shared.PacketWaypointAdd;
import co.crystaldev.client.network.plugin.shared.PacketWaypointRemove;
import co.crystaldev.client.util.objects.Waypoint;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  public void handleDisallowedFeatures(PacketDisallowedModules packetIn) {
    System.out.println("does this ever happen though, handleDisallowed");
    ModuleHandler.getInstance().updateDisallowedModules(packetIn.getDisallowedFeatures());
//     List<String> disabled = (List<String>)packetIn.getDisallowedFeatures().stream().filter(m -> ModuleHandler.getModules().stream().anyMatch(())).map(m -> WordUtils.capitalizeFully(m.toLowerCase().replace("_", " "))).collect(Collectors.toList());
    List<String> disabled = packetIn.getDisallowedFeatures().stream()
            .filter(m -> ModuleHandler.getModules().stream()
                    .anyMatch(module -> Boolean.parseBoolean((m))))
            .map(m -> WordUtils.capitalizeFully(m.toLowerCase().replace("_", " ")))
            .collect(Collectors.toList());
    if (!disabled.isEmpty())
      NotificationHandler.addNotification("The current server has remotely disabled: " + String.join(", ", disabled));
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
    System.out.println("handleaddwaypoint");
    WaypointHandler.getInstance().addWaypoint(packetIn.getWaypoint());
  }

  public void handleRemoveWaypoint(PacketWaypointRemove packetIn) {
    Waypoint waypoint = packetIn.getWaypoint();
    WaypointHandler.getInstance().removeWaypointIf(wp ->
        (wp.isServerSided() && wp.getPos().equals(waypoint.getPos()) && wp.getName().equals(waypoint.getName())));
  }
}
