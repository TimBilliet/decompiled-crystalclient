package mchorse.emoticons.skin_n_bones.api.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mchorse.emoticons.skin_n_bones.api.animation.json.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorActionsConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    public Map<String, AnimationEntry> animations = new HashMap<>();

    public Map<String, AnimatorConfig.AnimatorConfigEntry> configs = new HashMap<>();

    public AnimatorConfig.AnimatorConfigEntry defaultConfig;

    public Gson gson;

    public static final AnimationManager INSTANCE = new AnimationManager();

    private AnimationManager() {
        this.defaultConfig = new AnimatorConfig.AnimatorConfigEntry(new AnimatorConfig(), 0L);
        this.defaultConfig.config.rightHands.put("right_hand", new AnimatorHeldItemConfig("right_hand"));
        this.defaultConfig.config.leftHands.put("left_hand", new AnimatorHeldItemConfig("left_hand"));
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(AnimationMeshConfig.class, new AnimationMeshConfigAdapter());
        gson.registerTypeAdapter(AnimatorConfig.class, new AnimatorConfigAdapter());
        gson.registerTypeAdapter(AnimatorActionsConfig.class, new AnimatorActionsConfigAdapter());
        gson.registerTypeAdapter(AnimatorHeldItemConfig.class, new AnimatorHeldItemConfigAdapter());
        gson.registerTypeAdapter(ActionConfig.class, new ActionConfigAdapter());
        this.gson = gson.create();
    }

    public Animation getAnimation(String name) {
        AnimationEntry entry = this.animations.get(name);
        return (entry == null) ? null : entry.animation;
    }

    public AnimatorConfig.AnimatorConfigEntry getConfig(String name) {
        AnimatorConfig.AnimatorConfigEntry entry = this.configs.get(name);
        return (entry == null) ? this.defaultConfig : entry;
    }

    public static class AnimationEntry {
        public Animation animation;

        public File directory;

        public long lastModified;

        public AnimationEntry(Animation animation, File directory, long lastModified) {
            this.animation = animation;
            this.directory = directory;
            this.lastModified = lastModified;
        }

        public void reloadAnimation(BOBJLoader.BOBJData data, long lastModified) {
            this.animation.reload(data);
            this.lastModified = lastModified;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\AnimationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */