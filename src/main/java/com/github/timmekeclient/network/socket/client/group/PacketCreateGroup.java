package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;

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
