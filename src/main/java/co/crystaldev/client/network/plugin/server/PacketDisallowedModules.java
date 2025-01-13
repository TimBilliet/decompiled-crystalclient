package co.crystaldev.client.network.plugin.server;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.ReadOnly;
import co.crystaldev.client.network.plugin.NetHandlerPlugin;
import co.crystaldev.client.network.plugin.PluginChannelPacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ReadOnly
public class PacketDisallowedModules extends PluginChannelPacket {
    private final Set<String> disallowedFeatures = new HashSet<>();

    public Set<String> getDisallowedFeatures() {
        return this.disallowedFeatures;
    }

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        for (int i = 0; i < in.readVarInt(); i++)
            this.disallowedFeatures.add(in.readString());
    }

    public void process(NetHandlerPlugin handler) {
        handler.handleDisallowedFeatures(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\server\PacketDisallowedModules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */