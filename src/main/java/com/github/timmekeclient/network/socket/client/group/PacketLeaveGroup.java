package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;

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
