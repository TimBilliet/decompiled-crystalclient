package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.ChunkHighlight;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;

import java.io.IOException;

public class PacketHighlightChunk extends Packet {
    private int x;

    private int z;

    private String data;

    private ChunkHighlight chunk;

    private String server;

    public PacketHighlightChunk(int x, int z, String data) {
        this.x = x;
        this.z = z;
        this.data = data;
    }

    public PacketHighlightChunk() {
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeVarInt(this.x);
        out.writeVarInt(this.z);
        out.writeString(this.data);
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.chunk = (ChunkHighlight) Reference.GSON.fromJson(in.readString(), ChunkHighlight.class);
        this.server = in.readString();
    }

    public void process(INetHandler handler) {
        Group sg = GroupManager.getSelectedGroup();
        if (sg != null)
            sg.highlightChunk(this.server, this.chunk);
    }
}