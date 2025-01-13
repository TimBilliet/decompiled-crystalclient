package co.crystaldev.client.network.plugin.server;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.ReadOnly;
import co.crystaldev.client.network.plugin.NetHandlerPlugin;
import co.crystaldev.client.network.plugin.PluginChannelPacket;

import java.io.IOException;

@ReadOnly
public class PacketNotification extends PluginChannelPacket {
    private String title;

    private String content;

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.title = (this.title = in.readString()).substring(0, Math.min(this.title.length(), 30));
        this.content = (this.content = in.readString()).substring(0, Math.min(this.content.length(), 250));
    }

    public void process(NetHandlerPlugin handler) {
        handler.handleNotification(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\server\PacketNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */