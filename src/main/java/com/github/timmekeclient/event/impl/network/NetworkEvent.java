package com.github.timmekeclient.event.impl.network;

import com.github.timmekeclient.event.Event;
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
