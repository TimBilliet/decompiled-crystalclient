package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.mclib.utils.resources.RLUtils;

import java.lang.reflect.Type;

public class AnimationMeshConfigAdapter implements JsonDeserializer<AnimationMeshConfig>, JsonSerializer<AnimationMeshConfig> {
  public AnimationMeshConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (!json.isJsonObject())
      return null; 
    JsonObject object = json.getAsJsonObject();
    AnimationMeshConfig config = new AnimationMeshConfig();
    if (object.has("texture"))
      config.texture = RLUtils.create(object.get("texture")); 
    if (object.has("filtering"))
      config.filtering = object.get("filtering").getAsString().equalsIgnoreCase("linear") ? 9729 : 9728; 
    if (object.has("normals"))
      config.normals = object.get("normals").getAsBoolean(); 
    if (object.has("smooth"))
      config.smooth = object.get("smooth").getAsBoolean(); 
    if (object.has("visible"))
      config.visible = object.get("visible").getAsBoolean(); 
    if (object.has("lighting"))
      config.lighting = object.get("lighting").getAsBoolean(); 
    if (object.has("color"))
      config.color = object.get("color").getAsInt(); 
    return config;
  }
  
  public JsonElement serialize(AnimationMeshConfig src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    if (src.texture != null)
      object.add("texture", RLUtils.writeJson(src.texture)); 
    object.addProperty("filtering", (src.filtering == 9729) ? "linear" : "nearest");
    object.addProperty("normals", Boolean.valueOf(src.normals));
    object.addProperty("smooth", Boolean.valueOf(src.smooth));
    object.addProperty("visible", Boolean.valueOf(src.visible));
    object.addProperty("lighting", Boolean.valueOf(src.lighting));
    object.addProperty("color", Integer.valueOf(src.color));
    return (JsonElement)object;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\json\AnimationMeshConfigAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */