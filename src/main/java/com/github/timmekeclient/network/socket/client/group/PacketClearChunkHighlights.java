package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;

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
