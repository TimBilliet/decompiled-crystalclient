package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorActionsConfig;

import java.lang.reflect.Type;
import java.util.Map;

public class AnimatorActionsConfigAdapter implements JsonDeserializer<AnimatorActionsConfig> {
    public AnimatorActionsConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject())
            return null;
        JsonObject object = json.getAsJsonObject();
        AnimatorActionsConfig config = new AnimatorActionsConfig();
        for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) object.entrySet()) {
            JsonElement element = entry.getValue();
            String key = config.toKey(entry.getKey());
            if (element.isJsonObject())
                ((JsonObject) element).addProperty("name", key);
            config.actions.put(key, context.deserialize(element, ActionConfig.class));
        }
        return config;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\json\AnimatorActionsConfigAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */