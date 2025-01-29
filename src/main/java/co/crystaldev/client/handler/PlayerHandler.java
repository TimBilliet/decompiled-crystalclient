package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.network.socket.client.PacketOnlineUsers;
import co.crystaldev.client.network.socket.client.PacketServerConnection;
import co.crystaldev.client.network.socket.client.cosmetic.PacketRequestCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerHandler implements IRegistrable {
    private static PlayerHandler INSTANCE;

    private final Set<UUID> checkCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Set<UUID> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Set<UUID> orbitUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public Set<UUID> getCheckCache() {
        return this.checkCache;
    }

    public Set<UUID> getOnlineUsers() {
        return this.onlineUsers;
    }
    public Set<UUID> getOrbitUsers(){
        return this.orbitUsers;
    }

    private long lastLoginTime = 0L;

    private boolean transmitAll = false;

    private long lastConnectTime = 0L;

    private long lastOrbitCheck = 0L;

    public PlayerHandler() {
        INSTANCE = this;
    }

    public static PlayerHandler getInstance() {
        return INSTANCE;
    }

    public void loadOrbitPlayers() {
        this.lastOrbitCheck = System.currentTimeMillis();
        (new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://api.orbitclient.com/public/getOnlinePlayers");
                connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                connection.setRequestMethod("GET");
                String[] orbitPlayers = Reference.GSON.fromJson(new InputStreamReader(connection.getInputStream()), String[].class);
                Set<UUID> orbitplayersIds = Arrays.stream(orbitPlayers).map(UUID::fromString).collect(Collectors.toSet());
                for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                    if (orbitplayersIds.contains(player.getGameProfile().getId())) {
                        orbitUsers.add(player.getGameProfile().getId());
                        ((NetworkPlayerInfoExt) player).setOrbitOnlineStatus(true);
                    }
                }
            } catch (Exception var7) {
                var7.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        })).start();
    }

    public void registerEvents() {
        EventBus.register(this, PacketReceivedEvent.Post.class, ev -> {
            if (ev.packet instanceof S38PacketPlayerListItem) {
                S38PacketPlayerListItem packet = (S38PacketPlayerListItem) ev.packet;
                for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet.getEntries()) {
                    if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                        UUID uuid = addPlayerData.getProfile().getId();
                        this.lastLoginTime = System.currentTimeMillis();
                        this.checkCache.add(uuid);
                        UsernameCache.getInstance().getUsername(uuid);
                        loadOrbitPlayers();
                        continue;
                    }
                    if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                        this.onlineUsers.remove(addPlayerData.getProfile().getId());
                        this.orbitUsers.remove(addPlayerData.getProfile().getId());
                    }
                }
            }
        });
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (Minecraft.getMinecraft().getNetHandler() != null && (Minecraft.getMinecraft()).theWorld != null) {
                if (System.currentTimeMillis() - this.lastLoginTime > 2500L && !this.checkCache.isEmpty() && !this.transmitAll) {
                    Client.sendPacket(new PacketOnlineUsers(this.checkCache));
                    Client.sendPacket(new PacketRequestCosmetics(this.checkCache));
                    this.checkCache.clear();
                }
                if (this.transmitAll && System.currentTimeMillis() - this.lastConnectTime > 500L) {
                    this.checkCache.clear();
                    for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())
                        this.checkCache.add(info.getGameProfile().getId());
                    Client.sendPacket(new PacketOnlineUsers(this.checkCache));
                    Client.sendPacket(new PacketRequestCosmetics(this.checkCache));
                    this.checkCache.clear();
                    this.transmitAll = false;
                }
                if (System.currentTimeMillis() - this.lastOrbitCheck >= 30000L) {
                    loadOrbitPlayers();
                }
            }
        });
        EventBus.register(this, WorldEvent.Load.class, ev -> {
            if (Minecraft.getMinecraft().getNetHandler() != null) {
                this.transmitAll = true;
                this.lastConnectTime = System.currentTimeMillis();
                if (Minecraft.getMinecraft().theWorld != null)
                    loadOrbitPlayers();
            }
        });
        EventBus.register(this, ServerConnectEvent.class, ev -> {
            this.transmitAll = true;
            this.lastConnectTime = System.currentTimeMillis();
            Client.sendPacket(new PacketServerConnection(PacketServerConnection.Type.JOIN));
            if (Minecraft.getMinecraft().theWorld != null)
                loadOrbitPlayers();
        });
        EventBus.register(this, ServerDisconnectEvent.class, ev -> {
            Client.sendPacket(new PacketServerConnection(PacketServerConnection.Type.LEAVE));
            CosmeticCache.getInstance().clear();
            this.onlineUsers.clear();
            this.orbitUsers.clear();
        });
    }
}