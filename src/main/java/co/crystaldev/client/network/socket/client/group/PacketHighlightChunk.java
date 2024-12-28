package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.ChunkHighlight;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

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

  public PacketHighlightChunk() {}

  public void write(ByteBufWrapper out) throws IOException {
    out.writeVarInt(this.x);
    out.writeVarInt(this.z);
    out.writeString(this.data);
  }

  public void read(ByteBufWrapper in) throws IOException {
    this.chunk = (ChunkHighlight)Reference.GSON.fromJson(in.readString(), ChunkHighlight.class);
    this.server = in.readString();
  }

  public void process(INetHandler handler) {
    Group sg = GroupManager.getSelectedGroup();
    if (sg != null)
      sg.highlightChunk(this.server, this.chunk);
  }
}