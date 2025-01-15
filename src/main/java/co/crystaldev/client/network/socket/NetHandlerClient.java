package co.crystaldev.client.network.socket;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.WebClient;
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