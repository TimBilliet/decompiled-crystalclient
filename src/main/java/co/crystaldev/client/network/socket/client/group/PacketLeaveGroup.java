package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;

public class PacketLeaveGroup extends Packet {
    private final String groupId;

    public PacketLeaveGroup() {
        this.groupId = null;
    }

    public PacketLeaveGroup(String groupId) {
        this.groupId = groupId;
    }

    public void write(ByteBufWrapper out) throws IOException {
        if (this.groupId != null)
            out.writeString(this.groupId);
    }

    public void read(ByteBufWrapper in) throws IOException {
    }

    public void process(INetHandler handler) {
    }
}
