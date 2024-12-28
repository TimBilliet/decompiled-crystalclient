package mchorse.emoticons.common.emotes;

import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

public class StarPowerEmote extends Emote {
  public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
    if (tick == 30) {
      BOBJBone hand = (BOBJBone)armature.bones.get("low_right_arm.end");
      Vector4f result = animator.calcPosition(entity, hand, 0.0F, 0.15F, 0.0F, partial);
      for (int i = 0, c = 15; i < c; i++);
    } 
    if (tick >= 33 && tick < 43) {
      BOBJBone hand = (BOBJBone)armature.bones.get("low_right_arm.end");
      Vector4f result = animator.calcPosition(entity, hand, 0.0F, 0.15F, 0.0F, partial);
      float r = 1.0F;
      float g = 0.0F;
      float b = 0.0F;
      float p = (tick - 33) / 10.0F;
      if (p >= 0.2D)
        if (p < 0.35D) {
          g = 0.5F;
        } else if (p < 0.45D) {
          g = 1.0F;
        } else if (p < 0.65D) {
          r = 0.25F;
          g = 1.0F;
        } else if (p < 0.85D) {
          r = 0.0F;
          g = 0.75F;
          b = 1.0F;
        } else {
          r = 0.0F;
          g = 0.0F;
          b = 1.0F;
        }  
      for (int i = 0, c = 7; i < c; i++)
        entity.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, result.x + this.rand.nextDouble() * 0.05D - 0.025D, result.y + this.rand.nextDouble() * 0.05D - 0.025D, result.z + this.rand.nextDouble() * 0.05D - 0.025D, r, g, b, new int[0]); 
    } 
  }
  
  public StarPowerEmote(String name, int duration, boolean looping, String sound) {
    super(name, duration, looping, sound);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\StarPowerEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */