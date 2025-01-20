package co.crystaldev.client.event.impl.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class ServerConnectEvent extends NetworkEvent {
    private final boolean local;

    public boolean isLocal() {
        return this.local;
    }

    public ServerConnectEvent(NetworkManager manager) {
        super((INetHandlerPlayClient) manager.getNetHandler(), manager);
        this.local = manager.isLocalChannel();
    }
}
