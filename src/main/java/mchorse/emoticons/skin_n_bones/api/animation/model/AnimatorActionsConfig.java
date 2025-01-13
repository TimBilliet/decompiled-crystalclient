package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class AnimatorActionsConfig {
    public Map<String, ActionConfig> actions = new HashMap<>();

    public void copy(AnimatorActionsConfig config) {
        this.actions.clear();
        this.actions.putAll(config.actions);
    }

    public void fromNBT(NBTTagCompound tag) {
        this.actions.clear();
        for (String key : tag.getKeySet()) {
            NBTBase base = tag.getTag(key);
            String newKey = toKey(key);
            ActionConfig config = new ActionConfig(newKey);
            config.fromNBT(base);
            this.actions.put(newKey, config);
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound tag) {
        if (this.actions.isEmpty())
            return null;
        if (tag == null)
            tag = new NBTTagCompound();
        for (Map.Entry<String, ActionConfig> entry : this.actions.entrySet()) {
            ActionConfig action = entry.getValue();
            String key = entry.getKey();
            if (!key.equals(action.name) || !action.isDefault())
                tag.setTag(key, action.toNBT());
        }
        return tag;
    }

    public ActionConfig getConfig(String key) {
        ActionConfig output = this.actions.get(key);
        return (output == null) ? new ActionConfig(key) : output;
    }

    public String toKey(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\AnimatorActionsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */