package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.GroupSchematic;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.enums.EnumActionShift;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketGroupSchematicAction extends Packet {
  private GroupSchematic schematic;

  private EnumActionShift action;

  public PacketGroupSchematicAction() {}

  public PacketGroupSchematicAction(GroupSchematic schematic, EnumActionShift action) {
    this.schematic = schematic;
    this.action = action;
  }

  public void write(ByteBufWrapper out) throws IOException {
    JsonObject obj = new JsonObject();
    obj.addProperty("action", this.action.toString());
    obj.addProperty("schematic", Reference.GSON.toJson(this.schematic));
    out.writeString(Reference.GSON.toJson((JsonElement)obj));
  }

  public void read(ByteBufWrapper in) throws IOException {
    JsonObject obj = (JsonObject)Reference.GSON.fromJson(in.readString(), JsonObject.class);
    this.action = EnumActionShift.fromString(obj.get("action").getAsString());
    this.schematic = (GroupSchematic)Reference.GSON.fromJson(obj.get("schematic").getAsString(), GroupSchematic.class);
  }

  public void process(INetHandler handler) {
    Group group = GroupManager.getSelectedGroup();
    if (group != null)
      if (this.action == EnumActionShift.ADD) {
        if (group.hasPermission(9))
          group.addSchematic(this.schematic);
      } else if (this.action == EnumActionShift.REMOVE &&
        group.hasPermission(10)) {
        group.removeSchematic(this.schematic);
      }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupSchematicAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */