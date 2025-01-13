package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;

import java.lang.reflect.Type;

public class ActionConfigAdapter implements JsonDeserializer<ActionConfig> {
    public ActionConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ActionConfig config = new ActionConfig();
        if (json.isJsonObject()) {
            JsonObject object = (JsonObject) json;
            if (object.has("name"))
                config.name = object.get("name").getAsString();
            if (object.has("clamp"))
                config.clamp = object.get("clamp").getAsBoolean();
            if (object.has("reset"))
                config.reset = object.get("reset").getAsBoolean();
            if (object.has("speed"))
                config.speed = object.get("speed").getAsFloat();
            if (object.has("fade"))
                config.fade = object.get("fade").getAsInt();
            if (object.has("tick"))
                config.tick = object.get("tick").getAsInt();
        } else if (json.isJsonPrimitive()) {
            config.name = json.getAsString();
        }
        return config;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\json\ActionConfigAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */