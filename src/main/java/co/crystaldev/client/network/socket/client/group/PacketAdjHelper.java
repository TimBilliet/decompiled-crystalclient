package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.impl.factions.AdjustHelper;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.objects.Adjust;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.BlockPos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketAdjHelper extends Packet {
  private List<Adjust> adjusts;

  private UUID id;

  public void write(ByteBufWrapper out) throws IOException {
    JsonArray arr = new JsonArray();
    for (Adjust adjust : AdjustHelper.getInstance().getBestAdjusts()) {
      JsonObject obj = new JsonObject();
      obj.addProperty("originX", Integer.valueOf(adjust.origin.getX()));
      obj.addProperty("originY", Integer.valueOf(adjust.origin.getY()));
      obj.addProperty("originZ", Integer.valueOf(adjust.origin.getZ()));
      obj.addProperty("finishX", Integer.valueOf(adjust.finish.getX()));
      obj.addProperty("finishY", Integer.valueOf(adjust.finish.getY()));
      obj.addProperty("finishZ", Integer.valueOf(adjust.finish.getZ()));
      obj.addProperty("patches", Integer.valueOf(adjust.patches));
      obj.addProperty("patchIndex", Double.valueOf(adjust.patchIndex));
      obj.addProperty("coordText", adjust.coordText);
      arr.add((JsonElement)obj);
    }
    out.writeString(Reference.GSON.toJson(arr, JsonArray.class));
  }

  public void read(ByteBufWrapper in) throws IOException {
    this.id = in.readUUID();
    boolean fromSameServer = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().anyMatch(pl -> pl.getGameProfile().getId().equals(this.id));
    if ((Minecraft.getMinecraft()).theWorld != null && fromSameServer) {
      int index = 0;
      this.adjusts = new ArrayList<>();
      for (JsonElement elem : Reference.GSON.fromJson(in.readString(), JsonArray.class)) {
        if (index >= 10)
          return;
        JsonObject obj = (JsonObject)Reference.GSON.fromJson(elem.getAsString(), JsonObject.class);
        this.adjusts.add(new Adjust(new BlockPos(obj
                .get("originX").getAsInt(), obj.get("originY").getAsInt(), obj.get("originZ").getAsInt()), new BlockPos(obj
                .get("finishX").getAsInt(), obj.get("finishY").getAsInt(), obj.get("finishZ").getAsInt()), obj
              .get("patches").getAsInt(), obj
              .get("patchIndex").getAsDouble(), obj
              .get("coordText").getAsString()));
        index++;
      }
    }
  }

  public void process(INetHandler handler) {
    if ((Minecraft.getMinecraft()).theWorld == null || Minecraft.getMinecraft().getNetHandler() == null ||
      !(AdjustHelper.getInstance()).enabled || !(GroupOptions.getInstance()).sharedAdjusts || this.adjusts == null || this.adjusts
      .isEmpty())
      return;
    for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
      if (player.getGameProfile().getId().equals(this.id)) {
        Client.sendMessage("Incoming group Adjust Helper set from " + player.getGameProfile().getName(), true);
        AdjustHelper.getInstance().setAdjusts(this.adjusts);
      }
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketAdjHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */