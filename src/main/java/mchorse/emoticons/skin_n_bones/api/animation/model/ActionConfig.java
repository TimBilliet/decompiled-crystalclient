package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class ActionConfig {
    public String name = "";

    public boolean clamp = true;

    public boolean reset = true;

    public float speed = 1.0F;

    public float fade = 5.0F;

    public int tick = 0;

    public ActionConfig(String name) {
        this.name = name;
    }

    public ActionConfig clone() {
        ActionConfig config = new ActionConfig(this.name);
        config.clamp = this.clamp;
        config.reset = this.reset;
        config.speed = this.speed;
        config.fade = this.fade;
        config.tick = this.tick;
        return config;
    }

    public void fromNBT(NBTBase base) {
        if (base instanceof NBTTagCompound) {
            NBTTagCompound tag = (NBTTagCompound) base;
            if (tag.hasKey("Name", 8))
                this.name = tag.getString("Name");
            if (tag.hasKey("Clamp", 99))
                this.clamp = tag.getBoolean("Clamp");
            if (tag.hasKey("Reset", 99))
                this.reset = tag.getBoolean("Reset");
            if (tag.hasKey("Speed", 99))
                this.speed = tag.getFloat("Speed");
            if (tag.hasKey("Fade", 99))
                this.fade = tag.getInteger("Fade");
            if (tag.hasKey("Tick", 99))
                this.tick = tag.getInteger("Tick");
        } else if (base instanceof NBTTagString) {
            this.name = ((NBTTagString) base).getString();
        }
    }

    public NBTBase toNBT() {
        if (!this.name.isEmpty() && isDefault())
            return (NBTBase) new NBTTagString(this.name);
        NBTTagCompound tag = new NBTTagCompound();
        if (!this.name.isEmpty())
            tag.setString("Name", this.name);
        if (this.clamp != true)
            tag.setBoolean("Clamp", this.clamp);
        if (this.reset != true)
            tag.setBoolean("Reset", this.reset);
        if (this.speed != 1.0F)
            tag.setFloat("Speed", this.speed);
        if (this.fade != 5.0F)
            tag.setInteger("Fade", (int) this.fade);
        if (this.tick != 0)
            tag.setInteger("Tick", this.tick);
        return (NBTBase) tag;
    }

    public boolean isDefault() {
        return (this.clamp && this.reset && this.speed == 1.0F && this.fade == 5.0F && this.tick == 0);
    }

    public ActionConfig() {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\ActionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */