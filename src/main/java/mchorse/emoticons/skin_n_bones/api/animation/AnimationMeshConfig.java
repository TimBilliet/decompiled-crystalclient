package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class AnimationMeshConfig {
    public ResourceLocation texture;

    public int filtering = 9728;

    public boolean normals = false;

    public boolean smooth = false;

    public boolean visible = true;

    public boolean lighting = true;

    public int color = 16777215;

    public AnimationMeshConfig clone() {
        AnimationMeshConfig config = new AnimationMeshConfig();
        config.texture = this.texture;
        config.filtering = this.filtering;
        config.normals = this.normals;
        config.smooth = this.smooth;
        config.visible = this.visible;
        config.lighting = this.lighting;
        config.color = this.color;
        return config;
    }

    public void fromNBT(NBTTagCompound tag) {
        if (tag.hasKey("Texture"))
            this.texture = RLUtils.create(tag.getTag("Texture"));
        if (tag.hasKey("Filtering", 8))
            this.filtering = tag.getString("Filtering").equalsIgnoreCase("linear") ? 9729 : 9728;
        if (tag.hasKey("Normals", 99))
            this.normals = tag.getBoolean("Normals");
        if (tag.hasKey("Smooth", 99))
            this.smooth = tag.getBoolean("Smooth");
        if (tag.hasKey("Visible", 99))
            this.visible = tag.getBoolean("Visible");
        if (tag.hasKey("Lighting", 99))
            this.lighting = tag.getBoolean("Lighting");
        if (tag.hasKey("Color", 99))
            this.color = tag.getInteger("Color");
    }

    public NBTTagCompound toNBT(NBTTagCompound tag) {
        if (tag == null)
            tag = new NBTTagCompound();
        if (this.texture != null)
            tag.setTag("Texture", RLUtils.writeNbt(this.texture));
        tag.setString("Filtering", (this.filtering == 9728) ? "nearest" : "linear");
        tag.setBoolean("Normals", this.normals);
        tag.setBoolean("Smooth", this.smooth);
        tag.setBoolean("Visible", this.visible);
        tag.setBoolean("Lighting", this.lighting);
        tag.setInteger("Color", this.color);
        return tag;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\AnimationMeshConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */