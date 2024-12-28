package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.impl.factions.Patchcrumbs;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.UsernameUtils;
import co.crystaldev.client.util.objects.crumbs.Patchcrumb;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.UUID;

public class PacketPatchcrumbUpdate extends Packet {
  private int x;

  private int y;

  private int z;

  private UUID sender;

  private Patchcrumb.Direction direction = null;

  public PacketPatchcrumbUpdate() {}

  public PacketPatchcrumbUpdate(int x, int y, int z, Patchcrumb.Direction direction) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.direction = direction;
  }

  public void write(ByteBufWrapper out) throws IOException {
    JsonObject obj = new JsonObject();
    obj.addProperty("x", Integer.valueOf(this.x));
    obj.addProperty("y", Integer.valueOf(this.y));
    obj.addProperty("z", Integer.valueOf(this.z));
    obj.addProperty("direction", this.direction.toString());
    out.writeString(Reference.GSON.toJson(obj, JsonObject.class));
    out.writeUUID((Minecraft.getMinecraft()).thePlayer.getUniqueID());
  }

  public void read(ByteBufWrapper in) throws IOException {
    this.sender = in.readUUID();
    JsonObject obj = (JsonObject)Reference.GSON.fromJson(in.readString(), JsonObject.class);
    this.x = obj.get("x").getAsInt();
    this.y = obj.get("y").getAsInt();
    this.z = obj.get("z").getAsInt();
    if (obj.has("direction"))
      this.direction = Patchcrumb.Direction.fromString(obj.get("direction").getAsString());
  }

  public void process(INetHandler handler) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.thePlayer != null && mc.theWorld != null && !this.sender.equals(mc.thePlayer.getUniqueID()))
      if ((GroupOptions.getInstance()).sharedCrumb && (Patchcrumbs.getInstance()).enabled &&
        UsernameUtils.isOnline(this.sender))
        Patchcrumbs.getInstance().makeCrumb(this.x, this.y, this.z, this.direction, Patchcrumb.Source.GROUP);
  }
}
