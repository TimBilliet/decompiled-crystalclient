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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\ServerConnectEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */