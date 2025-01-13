package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;

public class PacketClearChunkHighlights extends Packet {
    private String server;

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.server = in.readString();
    }

    public void process(INetHandler handler) {
        Group sg = GroupManager.getSelectedGroup();
        if (sg != null)
            sg.clearHighlightedChunks(this.server);
    }
}
