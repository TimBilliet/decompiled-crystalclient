package co.crystaldev.client.network.socket.client.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.CosmeticType;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.*;

public class PacketRequestCosmetics extends Packet {
  private final List<UUID> ids = new ArrayList<>();
  
  private String json;
  
  public PacketRequestCosmetics(UUID... ids) {
    this.ids.addAll(Arrays.asList(ids));
  }
  
  public PacketRequestCosmetics(Set<UUID> ids) {
    this.ids.addAll(ids);
  }
  
  public PacketRequestCosmetics() {}
  
  public void write(ByteBufWrapper out) throws IOException {
    JsonObject obj = new JsonObject();
    JsonArray arr = new JsonArray();
    for (UUID id : this.ids)
      arr.add((JsonElement)new JsonPrimitive(id.toString())); 
    obj.add("uuids", (JsonElement)arr);
    out.writeString(arr.toString());
  }
  
  public void read(ByteBufWrapper in) throws IOException {
    this.json = in.readString();
  }
  
  public void process(INetHandler handler) {
    JsonArray arr = (JsonArray)Reference.GSON.fromJson(this.json, JsonArray.class);
    for (JsonElement elem : arr) {
      JsonObject obj = elem.getAsJsonObject();
      UUID id = UUID.fromString(obj.get("uuid").getAsString());
      String cosmetics = obj.get("cosmetics").getAsString();
      ArrayList<Cosmetic> list = new ArrayList<>();
      JsonObject obj1 = (JsonObject)Reference.GSON.fromJson(cosmetics, JsonObject.class);
      if (obj1.has("cloak")) {
        String current = obj1.getAsJsonObject("cloak").get("current").getAsString();
        list.add(current.equals("null") ? (Cosmetic)CosmeticManager.EMPTY_CLOAK : 
            
            CosmeticManager.getInstance().getCosmetic(current, CosmeticType.CLOAK));
      } 
      if (obj1.has("wings")) {
        String current = obj1.getAsJsonObject("wings").get("current").getAsString();
        list.add(current.equals("null") ? (Cosmetic)CosmeticManager.EMPTY_WINGS : 
            
            CosmeticManager.getInstance().getCosmetic(current, CosmeticType.WINGS));
      } 
      if (obj1.has("color")) {
        String current = obj1.getAsJsonObject("color").get("current").getAsString();
        list.add(current.equals("null") ? (Cosmetic)CosmeticManager.COLOR_WHITE : CosmeticManager.getInstance().getCosmetic(current, CosmeticType.COLOR));
      } 
      CosmeticManager.getInstance().addLoad(id, list);
      if (id.equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
        ArrayList<String> ownedList = new ArrayList<>();
        if (obj1.has("cloak")) {
          JsonArray owned = obj1.getAsJsonObject("cloak").get("owned").getAsJsonArray();
          boolean wildcardCloaks = false;
          for (JsonElement elem1 : owned) {
            if (elem1.getAsString().equals("null"))
              continue; 
            if (elem1.getAsString().equals("*")) {
              wildcardCloaks = true;
              continue;
            } 
            ownedList.add("cloak_" + elem1.getAsString());
          } 
          CosmeticManager.wildcardCloaks = wildcardCloaks;
        } 
        if (obj1.has("wings")) {
          JsonArray owned = obj1.getAsJsonObject("wings").get("owned").getAsJsonArray();
          boolean wildcardWings = false;
          for (JsonElement elem1 : owned) {
            if (elem1.getAsString().equals("null"))
              continue; 
            if (elem1.getAsString().equals("*")) {
              wildcardWings = true;
              continue;
            } 
            ownedList.add("wings_" + elem1.getAsString());
          } 
          CosmeticManager.wildcardWings = wildcardWings;
        } 
        if (obj1.has("color")) {
          JsonArray owned = obj1.getAsJsonObject("color").get("owned").getAsJsonArray();
          boolean wildcardColors = false;
          for (JsonElement elem1 : owned) {
            if (elem1.getAsString().equals("null"))
              continue; 
            if (elem1.getAsString().equals("*")) {
              wildcardColors = true;
              continue;
            } 
            ownedList.add("color_" + elem1.getAsString());
          } 
          CosmeticManager.wildcardColors = wildcardColors;
        } 
        if (obj1.has("emote")) {
          JsonArray owned = obj1.getAsJsonObject("emote").get("owned").getAsJsonArray();
          boolean wildcardEmotes = false;
          for (JsonElement elem1 : owned) {
            if (elem1.getAsString().equals("null"))
              continue; 
            if (elem1.getAsString().equals("*")) {
              wildcardEmotes = true;
              continue;
            } 
            ownedList.add("emote_" + elem1.getAsString());
          } 
          CosmeticManager.wildcardEmotes = wildcardEmotes;
        } 
        CosmeticManager.getInstance().populateOwned(ownedList);
      } 
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\cosmetic\PacketRequestCosmetics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */