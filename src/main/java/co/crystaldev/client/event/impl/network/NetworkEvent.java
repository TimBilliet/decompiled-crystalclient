package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Event;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public abstract class NetworkEvent extends Event {
    public final INetHandlerPlayClient handler;

    public final NetworkManager manager;

    public NetworkEvent(INetHandlerPlayClient handler, NetworkManager manager) {
        this.handler = handler;
        this.manager = manager;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\NetworkEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */