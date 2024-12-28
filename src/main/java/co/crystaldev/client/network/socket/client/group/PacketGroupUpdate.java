package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.screens.groups.ScreenGroups;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;

public class PacketGroupUpdate extends Packet {
  private Group selectedGroup;

  private String id;

  public PacketGroupUpdate() {}

  public PacketGroupUpdate(String id) {
    this.id = (id == null) ? "null" : id;
  }

  public void write(ByteBufWrapper out) throws IOException {
    out.writeString(this.id);
  }

  public void read(ByteBufWrapper in) throws IOException {
    this.selectedGroup = (Group)Reference.GSON.fromJson(in.readString(), Group.class);
  }

  public void process(INetHandler handler) {
    GroupManager.setSelectedGroup(this.selectedGroup);
    ScreenGroups.updateGroup();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupUpdate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */