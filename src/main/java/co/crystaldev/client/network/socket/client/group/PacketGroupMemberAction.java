package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;
import java.util.UUID;

public class PacketGroupMemberAction extends Packet {
  private UUID uuid;

  private Action action;

  public PacketGroupMemberAction() {}

  public PacketGroupMemberAction(UUID uuid, Action action) {
    this.uuid = uuid;
    this.action = action;
  }

  public void write(ByteBufWrapper out) throws IOException {
    out.writeUUID(this.uuid);
    out.writeString(this.action.toString());
  }

  public void read(ByteBufWrapper in) throws IOException {
    this.uuid = in.readUUID();
    this.action = Action.valueOf(in.readString());
  }

  public void process(INetHandler handler) {
    Group group = GroupManager.getSelectedGroup();
    if (group != null)
      if (this.action == Action.REMOVE) {
        group.removeMember(this.uuid);
      } else if (this.action == Action.ADD) {
        group.addMember(this.uuid);
      }
  }

  public enum Action {
    PROMOTE("PROMOTE"),
    DEMOTE("DEMOTE"),
    ADD("ADD"),
    REMOVE("REMOVE");

    Action(String serializationString) {
      this.serializationString = serializationString;
    }

    private final String serializationString;

    public String getSerializationString() {
      return this.serializationString;
    }

    public String toString() {
      return this.serializationString;
    }

    public static Action fromString(String name) {
      for (Action action : values()) {
        if (action.toString().equalsIgnoreCase(name))
          return action;
      }
      return null;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupMemberAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */