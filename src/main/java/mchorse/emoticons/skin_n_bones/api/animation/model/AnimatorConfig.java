package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class AnimatorConfig {
    public String name = "";

    public String primaryMesh = "";

    public float scale = 1.0F;

    public float scaleGui = 1.0F;

    public float scaleItems = 1.0F;

    public boolean renderHeldItems = true;

    public Map<String, AnimatorHeldItemConfig> leftHands = new HashMap<>();

    public Map<String, AnimatorHeldItemConfig> rightHands = new HashMap<>();

    public String head = "head";

    public AnimatorActionsConfig actions = new AnimatorActionsConfig();

    public Map<String, AnimationMeshConfig> meshes = new HashMap<>();

    public void copy(AnimatorConfig config) {
        this.name = config.name;
        this.primaryMesh = config.primaryMesh;
        this.scale = config.scale;
        this.scaleGui = config.scaleGui;
        this.scaleItems = config.scaleItems;
        this.renderHeldItems = config.renderHeldItems;
        this.head = config.head;
        this.actions.copy(config.actions);
        this.leftHands.clear();
        this.rightHands.clear();
        this.meshes.clear();
        for (Map.Entry<String, AnimatorHeldItemConfig> entry : config.leftHands.entrySet())
            this.leftHands.put(entry.getKey(), ((AnimatorHeldItemConfig) entry.getValue()).clone());
        for (Map.Entry<String, AnimatorHeldItemConfig> entry : config.rightHands.entrySet())
            this.rightHands.put(entry.getKey(), ((AnimatorHeldItemConfig) entry.getValue()).clone());
        for (Map.Entry<String, AnimationMeshConfig> entry : config.meshes.entrySet())
            this.meshes.put(entry.getKey(), ((AnimationMeshConfig) entry.getValue()).clone());
    }

    public void fromNBT(NBTTagCompound tag) {
        if (tag.hasKey("Name", 8))
            this.name = tag.getString("Name");
        if (tag.hasKey("Scale", 99))
            this.scale = tag.getFloat("Scale");
        if (tag.hasKey("ScaleGUI", 99))
            this.scaleGui = tag.getFloat("ScaleGUI");
        if (tag.hasKey("ScaleItems", 99))
            this.scaleItems = tag.getFloat("ScaleItems");
        if (tag.hasKey("RenderHeldItems", 99))
            this.renderHeldItems = tag.getBoolean("RenderHeldItems");
        if (tag.hasKey("LeftHands"))
            readHandsFromNBT(this.leftHands, tag.getTag("LeftHands"));
        if (tag.hasKey("RightHands"))
            readHandsFromNBT(this.rightHands, tag.getTag("RightHands"));
        if (tag.hasKey("Head", 8))
            this.head = tag.getString("Head");
        if (tag.hasKey("Actions", 10))
            this.actions.fromNBT(tag.getCompoundTag("Actions"));
        if (tag.hasKey("Meshes", 10)) {
            NBTTagCompound meshes = tag.getCompoundTag("Meshes");
            for (String key : meshes.getKeySet()) {
                NBTBase nbt = meshes.getTag(key);
                AnimationMeshConfig config = this.meshes.get(key);
                if (config == null)
                    this.meshes.put(key, config = new AnimationMeshConfig());
                if (nbt.getId() == 10)
                    config.fromNBT((NBTTagCompound) nbt);
            }
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound tag) {
        if (tag == null)
            tag = new NBTTagCompound();
        if (!this.name.isEmpty())
            tag.setString("Name", this.name);
        if (this.scale != 1.0F)
            tag.setFloat("Scale", this.scale);
        if (this.scaleGui != 1.0F)
            tag.setFloat("ScaleGUI", this.scaleGui);
        if (this.scaleItems != 1.0F)
            tag.setFloat("ScaleItems", this.scaleItems);
        if (this.renderHeldItems != true)
            tag.setBoolean("RenderHeldItems", this.renderHeldItems);
        if (!this.head.equals("head"))
            tag.setString("Head", this.head);
        if (!this.leftHands.isEmpty())
            tag.setTag("LeftHands", (NBTBase) writeHandsToNBT(this.leftHands));
        if (!this.rightHands.isEmpty())
            tag.setTag("RightHands", (NBTBase) writeHandsToNBT(this.rightHands));
        NBTTagCompound actions = this.actions.toNBT(null);
        if (actions != null && !actions.hasNoTags())
            tag.setTag("Actions", (NBTBase) actions);
        if (!this.meshes.isEmpty()) {
            NBTTagCompound meshes = new NBTTagCompound();
            for (Map.Entry<String, AnimationMeshConfig> entry : this.meshes.entrySet())
                meshes.setTag(entry.getKey(), (NBTBase) ((AnimationMeshConfig) entry.getValue()).toNBT(null));
            tag.setTag("Meshes", (NBTBase) meshes);
        }
        return tag;
    }

    private void readHandsFromNBT(Map<String, AnimatorHeldItemConfig> hands, NBTBase tag) {
        hands.clear();
        if (tag.getId() == 9) {
            NBTTagList list = (NBTTagList) tag;
            for (int i = 0, c = list.tagCount(); i < c; i++) {
                String key = list.getStringTagAt(i);
                hands.put(key, new AnimatorHeldItemConfig(key));
            }
        } else if (tag.getId() == 10) {
            NBTTagCompound compound = (NBTTagCompound) tag;
            for (String key : compound.getKeySet()) {
                AnimatorHeldItemConfig item = hands.get(key);
                if (item == null)
                    hands.put(key, item = new AnimatorHeldItemConfig(key));
                item.fromNBT(compound.getCompoundTag(key));
            }
        }
    }

    private NBTTagCompound writeHandsToNBT(Map<String, AnimatorHeldItemConfig> hands) {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<String, AnimatorHeldItemConfig> entry : hands.entrySet())
            tag.setTag(entry.getKey(), (NBTBase) ((AnimatorHeldItemConfig) entry.getValue()).toNBT(null));
        return tag;
    }

    public static class AnimatorConfigEntry {
        public AnimatorConfig config;

        public long lastModified;

        public AnimatorConfigEntry(AnimatorConfig config, long lastModified) {
            this.config = config;
            this.lastModified = lastModified;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\AnimatorConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */