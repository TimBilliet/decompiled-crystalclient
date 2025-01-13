package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.mclib.utils.Interpolation;
import net.minecraft.nbt.NBTTagCompound;

public class AnimatorHeldItemConfig {
    public String boneName = "";

    public float x;

    public float y;

    public float z;

    public float scaleX = 1.0F;

    public float scaleY = 1.0F;

    public float scaleZ = 1.0F;

    public float rotateX;

    public float rotateY;

    public float rotateZ;

    public AnimatorHeldItemConfig(String name) {
        this.boneName = name;
    }

    public void interpolate(AnimatorHeldItemConfig a, AnimatorHeldItemConfig b, float x, Interpolation interp) {
        this.x = interp.interpolate(a.x, b.x, x);
        this.y = interp.interpolate(a.y, b.y, x);
        this.z = interp.interpolate(a.z, b.z, x);
        this.scaleX = interp.interpolate(a.scaleX, b.scaleX, x);
        this.scaleY = interp.interpolate(a.scaleY, b.scaleY, x);
        this.scaleZ = interp.interpolate(a.scaleZ, b.scaleZ, x);
        this.rotateX = interp.interpolate(a.rotateX, b.rotateX, x);
        this.rotateY = interp.interpolate(a.rotateY, b.rotateY, x);
        this.rotateZ = interp.interpolate(a.rotateZ, b.rotateZ, x);
    }

    public AnimatorHeldItemConfig clone() {
        AnimatorHeldItemConfig item = new AnimatorHeldItemConfig(this.boneName);
        item.x = this.x;
        item.y = this.y;
        item.z = this.z;
        item.scaleX = this.scaleX;
        item.scaleY = this.scaleY;
        item.scaleZ = this.scaleZ;
        item.rotateX = this.rotateX;
        item.rotateY = this.rotateY;
        item.rotateZ = this.rotateZ;
        return item;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AnimatorHeldItemConfig) {
            AnimatorHeldItemConfig config = (AnimatorHeldItemConfig) obj;
            boolean result = (config.x == this.x && config.y == this.y && config.z == this.z);
            result = (result && config.scaleX == this.scaleX && config.scaleY == this.scaleY && config.scaleZ == this.scaleZ);
            result = (result && config.rotateX == this.rotateX && config.rotateY == this.rotateY && config.rotateZ == this.rotateZ);
            return result;
        }
        return super.equals(obj);
    }

    public void fromNBT(NBTTagCompound tag) {
        if (tag.hasKey("X", 99))
            this.x = tag.getFloat("X");
        if (tag.hasKey("Y", 99))
            this.y = tag.getFloat("Y");
        if (tag.hasKey("Z", 99))
            this.z = tag.getFloat("Z");
        if (tag.hasKey("SX", 99))
            this.scaleX = tag.getFloat("SX");
        if (tag.hasKey("SY", 99))
            this.scaleY = tag.getFloat("SY");
        if (tag.hasKey("SZ", 99))
            this.scaleZ = tag.getFloat("SZ");
        if (tag.hasKey("RX", 99))
            this.rotateX = tag.getFloat("RX");
        if (tag.hasKey("RY", 99))
            this.rotateY = tag.getFloat("RY");
        if (tag.hasKey("RZ", 99))
            this.rotateZ = tag.getFloat("RZ");
    }

    public NBTTagCompound toNBT(NBTTagCompound tag) {
        if (tag == null)
            tag = new NBTTagCompound();
        if (this.x != 0.0F)
            tag.setFloat("X", this.x);
        if (this.y != 0.0F)
            tag.setFloat("Y", this.y);
        if (this.z != 0.0F)
            tag.setFloat("Z", this.z);
        if (this.scaleX != 1.0F)
            tag.setFloat("SX", this.scaleX);
        if (this.scaleY != 1.0F)
            tag.setFloat("SY", this.scaleY);
        if (this.scaleZ != 1.0F)
            tag.setFloat("SZ", this.scaleZ);
        if (this.rotateX != 0.0F)
            tag.setFloat("RX", this.rotateX);
        if (this.rotateY != 0.0F)
            tag.setFloat("RY", this.rotateY);
        if (this.rotateZ != 0.0F)
            tag.setFloat("RZ", this.rotateZ);
        return tag;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\AnimatorHeldItemConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */