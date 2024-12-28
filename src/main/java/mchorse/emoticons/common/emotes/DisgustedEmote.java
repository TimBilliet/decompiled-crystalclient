package mchorse.emoticons.common.emotes;

import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.emoticons.utils.Time;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

public class DisgustedEmote extends Emote {
  public DisgustedEmote(String name, int duration, boolean looping, String sound) {
    super(name, duration, looping, sound);
  }
  
  public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
    super.progressAnimation(entity, armature, animator, tick, partial);
    if (tick >= Time.toTicks(117) && tick < Time.toTicks(140))
      for (int i = 0; i < 10; i++) {
        Vector4f result = animator.calcPosition(entity, (BOBJBone)armature.bones.get("head"), 0.0F, 0.125F, 0.25F, partial);
        entity.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, (result.x + rand(0.1F)), result.y, (result.z + rand(0.1F)), rand(0.05F), -0.125D, rand(0.05F), new int[] { 351, 2 });
      }  
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\DisgustedEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */