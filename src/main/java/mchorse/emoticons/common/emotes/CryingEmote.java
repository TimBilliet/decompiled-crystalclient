package mchorse.emoticons.common.emotes;

import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

public class CryingEmote extends Emote {
  public CryingEmote(String name, int duration, boolean looping, String sound) {
    super(name, duration, looping, sound);
  }
  
  public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
    if (tick % 2 == 0) {
      BOBJBone hand = (BOBJBone)armature.bones.get("head");
      Vector4f result = animator.calcPosition(entity, hand, 0.0F, 0.5F, 0.35F, partial);
      entity.worldObj.spawnParticle(EnumParticleTypes.WATER_DROP, result.x, result.y, result.z, 1.0D, -1.0D, 1.0D, new int[0]);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\CryingEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */