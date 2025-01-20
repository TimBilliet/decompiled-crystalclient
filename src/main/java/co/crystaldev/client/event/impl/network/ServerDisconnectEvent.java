package co.crystaldev.client.event.impl.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class ServerDisconnectEvent extends NetworkEvent {
    public ServerDisconnectEvent(NetworkManager manager) {
        super((INetHandlerPlayClient) manager.getNetHandler(), manager);
    }
}
