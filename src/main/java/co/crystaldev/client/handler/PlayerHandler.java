package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
//import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.PacketOnlineUsers;
import co.crystaldev.client.network.socket.client.PacketServerConnection;
import co.crystaldev.client.network.socket.client.cosmetic.PacketRequestCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHandler implements IRegistrable {
  private static PlayerHandler INSTANCE;

  private final Set<UUID> checkCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

  private final Set<UUID> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

  public Set<UUID> getCheckCache() {
    return this.checkCache;
  }

  public Set<UUID> getOnlineUsers() {
    return this.onlineUsers;
  }

  private long lastLoginTime = 0L;

  private boolean transmitAll = false;

  private long lastConnectTime = 0L;

  public PlayerHandler() {
    INSTANCE = this;
  }

  public static PlayerHandler getInstance() {
    return INSTANCE;
  }

  public void registerEvents() {
    EventBus.register(this, PacketReceivedEvent.Post.class, ev -> {
          if (ev.packet instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem)ev.packet;
            for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet.getEntries()) {
              if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                UUID uuid = addPlayerData.getProfile().getId();
                this.lastLoginTime = System.currentTimeMillis();
                this.checkCache.add(uuid);
                UsernameCache.getInstance().getUsername(uuid);
                continue;
              }
              if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER)
                this.onlineUsers.remove(addPlayerData.getProfile().getId());
            }
          }
        });
    EventBus.register(this, ClientTickEvent.Post.class, ev -> {
          if (Minecraft.getMinecraft().getNetHandler() != null && (Minecraft.getMinecraft()).theWorld != null) {
            if (System.currentTimeMillis() - this.lastLoginTime > 2500L && !this.checkCache.isEmpty() && !this.transmitAll) {
              Client.sendPacket((Packet)new PacketOnlineUsers(this.checkCache));
              Client.sendPacket((Packet)new PacketRequestCosmetics(this.checkCache));
              this.checkCache.clear();
            }
            if (this.transmitAll && System.currentTimeMillis() - this.lastConnectTime > 500L) {
              this.checkCache.clear();
              for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())
                this.checkCache.add(info.getGameProfile().getId());
              Client.sendPacket((Packet)new PacketOnlineUsers(this.checkCache));
              Client.sendPacket((Packet)new PacketRequestCosmetics(this.checkCache));
              this.checkCache.clear();
              this.transmitAll = false;
            }
          }
        });
    EventBus.register(this, WorldEvent.Load.class, ev -> {
          if (Minecraft.getMinecraft().getNetHandler() != null) {
            this.transmitAll = true;
            this.lastConnectTime = System.currentTimeMillis();
          }
        });
    EventBus.register(this, ServerConnectEvent.class, ev -> {
          this.transmitAll = true;
          this.lastConnectTime = System.currentTimeMillis();
          Client.sendPacket((Packet)new PacketServerConnection(PacketServerConnection.Type.JOIN));
        });
    EventBus.register(this, ServerDisconnectEvent.class, ev -> {
          Client.sendPacket((Packet)new PacketServerConnection(PacketServerConnection.Type.LEAVE));
          CosmeticCache.getInstance().clear();
          this.onlineUsers.clear();
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\handler\PlayerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */