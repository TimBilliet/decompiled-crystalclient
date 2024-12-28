package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.screens.groups.ScreenGroups;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;
import java.util.UUID;

public class PacketGroupMemberRankChange extends Packet {
  private UUID uuid;

  private Rank rank;

  public void write(ByteBufWrapper out) throws IOException {}

  public void read(ByteBufWrapper in) throws IOException {
    this.uuid = in.readUUID();
    this.rank = Rank.fromString(in.readString());
  }

  public void process(INetHandler handler) {
    Group group = GroupManager.getSelectedGroup();
    if (group != null) {
      group.getMember(this.uuid).setRank(this.rank);
      ScreenGroups.updateMembers();
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupMemberRankChange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */