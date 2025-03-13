package com.github.timmekeclient.network.socket;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;
import com.github.timmekeclient.network.WebClient;
import io.netty.buffer.ByteBuf;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class NetHandlerClient implements INetHandler {
    private static NetHandlerClient INSTANCE;

    public NetHandlerClient() {
        if (INSTANCE != null)
            EventBus.unregister(INSTANCE);
        INSTANCE = this;
    }

    public void sendPacket(Packet packet) {
        try {
            ByteBuf buf = Packet.getPacketBuf(packet);
            WebClient.getInstance().send(buf.array());
        } catch (WebsocketNotConnectedException ex) {
            Client.getInstance().connectToSocket(true);
        }
    }
}