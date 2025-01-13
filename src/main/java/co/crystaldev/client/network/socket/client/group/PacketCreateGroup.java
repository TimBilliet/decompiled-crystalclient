package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;

public class PacketCreateGroup extends Packet {
    private final String name;

    public PacketCreateGroup(String name) {
        this.name = name;
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.name);
    }

    public void read(ByteBufWrapper in) throws IOException {
    }

    public void process(INetHandler handler) {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketCreateGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */