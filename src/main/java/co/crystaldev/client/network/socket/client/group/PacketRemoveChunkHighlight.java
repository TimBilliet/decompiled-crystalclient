package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;

public class PacketRemoveChunkHighlight extends Packet {
    private int x;

    private int z;

    private String server;

    public PacketRemoveChunkHighlight(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public PacketRemoveChunkHighlight() {
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeVarInt(this.x);
        out.writeVarInt(this.z);
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.x = in.readVarInt();
        this.z = in.readVarInt();
        this.server = in.readString();
    }

    public void process(INetHandler handler) {
        Group sg = GroupManager.getSelectedGroup();
        if (sg != null)
            sg.removeHighlight(this.server, this.x, this.z);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketRemoveChunkHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */