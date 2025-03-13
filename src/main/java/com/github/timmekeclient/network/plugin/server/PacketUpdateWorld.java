package com.github.timmekeclient.network.plugin.server;

import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.ReadOnly;
import com.github.timmekeclient.network.plugin.NetHandlerPlugin;
import com.github.timmekeclient.network.plugin.PluginChannelPacket;

import java.io.IOException;

@ReadOnly
public class PacketUpdateWorld extends PluginChannelPacket {
    private String world;

    public String getWorld() {
        return this.world;
    }

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.world = in.readString();
    }

    public void process(NetHandlerPlugin handler) {
        handler.handleUpdateWorld(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\server\PacketUpdateWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */