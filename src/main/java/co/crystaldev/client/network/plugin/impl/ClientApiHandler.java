package co.crystaldev.client.network.plugin.impl;

import co.crystaldev.client.network.plugin.MessageHandler;
import co.crystaldev.client.network.plugin.NetHandlerPlugin;
import io.netty.buffer.ByteBuf;

public class ClientApiHandler extends MessageHandler {
    private final NetHandlerPlugin netHandler = new NetHandlerPlugin(this);

    public void fromBytes(ByteBuf buf) {
        this.netHandler.handlePacket(buf.array());
    }

    public void toBytes(ByteBuf buf) {
    }

    public void onMessage() {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\impl\ClientApiHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */