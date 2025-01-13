package co.crystaldev.client.event.impl.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class ServerDisconnectEvent extends NetworkEvent {
    public ServerDisconnectEvent(NetworkManager manager) {
        super((INetHandlerPlayClient) manager.getNetHandler(), manager);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\ServerDisconnectEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */