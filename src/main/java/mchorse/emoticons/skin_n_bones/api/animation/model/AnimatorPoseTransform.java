package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.NBTTagCompound;

public class AnimatorPoseTransform extends AnimatorHeldItemConfig {
  public static final int FIXED = 0;
  
  public static final int ANIMATED = 1;
  
  public float fixed = 1.0F;
  
  public AnimatorPoseTransform(String name) {
    super(name);
  }
  
  public AnimatorPoseTransform clone() {
    AnimatorPoseTransform item = new AnimatorPoseTransform(this.boneName);
    item.x = this.x;
    item.y = this.y;
    item.z = this.z;
    item.scaleX = this.scaleX;
    item.scaleY = this.scaleY;
    item.scaleZ = this.scaleZ;
    item.rotateX = this.rotateX;
    item.rotateY = this.rotateY;
    item.rotateZ = this.rotateZ;
    item.fixed = this.fixed;
    return item;
  }
  
  public boolean equals(Object obj) {
    boolean result = super.equals(obj);
    if (obj instanceof AnimatorPoseTransform)
      result = (result && this.fixed == ((AnimatorPoseTransform)obj).fixed); 
    return result;
  }
  
  public void fromNBT(NBTTagCompound tag) {
    super.fromNBT(tag);
    if (tag.hasKey("F"))
      this.fixed = tag.getBoolean("F") ? 1.0F : 0.0F; 
  }
  
  public NBTTagCompound toNBT(NBTTagCompound tag) {
    tag = super.toNBT(tag);
    if (this.fixed != 1.0F)
      tag.setBoolean("F", false); 
    return tag;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\AnimatorPoseTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */