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
