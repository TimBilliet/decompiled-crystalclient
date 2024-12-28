package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;

import java.lang.reflect.Type;

public class AnimatorHeldItemConfigAdapter implements JsonDeserializer<AnimatorHeldItemConfig> {
  public AnimatorHeldItemConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (!json.isJsonObject())
      return null; 
    JsonObject object = (JsonObject)json;
    AnimatorHeldItemConfig config = new AnimatorHeldItemConfig("");
    if (object.has("x"))
      config.x = object.get("x").getAsFloat(); 
    if (object.has("y"))
      config.y = object.get("y").getAsFloat(); 
    if (object.has("z"))
      config.z = object.get("z").getAsFloat(); 
    if (object.has("sx"))
      config.scaleX = object.get("sx").getAsFloat(); 
    if (object.has("sy"))
      config.scaleY = object.get("sy").getAsFloat(); 
    if (object.has("sz"))
      config.scaleZ = object.get("sz").getAsFloat(); 
    if (object.has("rx"))
      config.rotateX = object.get("rx").getAsFloat(); 
    if (object.has("ry"))
      config.rotateY = object.get("ry").getAsFloat(); 
    if (object.has("rz"))
      config.rotateZ = object.get("rz").getAsFloat(); 
    return config;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\json\AnimatorHeldItemConfigAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */