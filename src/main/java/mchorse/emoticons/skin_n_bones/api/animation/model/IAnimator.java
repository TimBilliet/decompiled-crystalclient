package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.EntityLivingBase;

public interface IAnimator {
  void refresh();
  
  void setEmote(ActionPlayback paramActionPlayback);
  
  void update(EntityLivingBase paramEntityLivingBase);
  
  BOBJArmature useArmature(BOBJArmature paramBOBJArmature);
  
  void applyActions(BOBJArmature paramBOBJArmature, float paramFloat);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\IAnimator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */