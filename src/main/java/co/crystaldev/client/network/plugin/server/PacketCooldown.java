package co.crystaldev.client.network.plugin.server;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.ReadOnly;
import co.crystaldev.client.network.plugin.NetHandlerPlugin;
import co.crystaldev.client.network.plugin.PluginChannelPacket;
import net.minecraft.item.ItemStack;

import java.io.IOException;

@ReadOnly
public class PacketCooldown extends PluginChannelPacket {
    private ItemStack itemStack;

    private long duration;

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public long getDuration() {
        return this.duration;
    }

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.itemStack = in.readItemStack();
        this.duration = in.buf().readLong();
    }

    public void process(NetHandlerPlugin handler) {
        handler.handleCooldown(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\server\PacketCooldown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */