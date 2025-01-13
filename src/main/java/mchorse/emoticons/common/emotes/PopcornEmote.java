package mchorse.emoticons.common.emotes;

import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.client.particles.PopcornParticle;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;

public class PopcornEmote extends Emote {
    public PopcornEmote(String name, int duration, boolean looping, String sound) {
        super(name, duration, looping, sound);
    }

    public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
        if (tick == 8 || tick == 32 || tick == 56 || tick == 86) {
            BOBJBone hand = (BOBJBone) armature.bones.get("low_right_arm.end");
            Vector4f result = animator.calcPosition(entity, hand, 0.0F, 0.15F, 0.0F, partial);
            for (int i = 0, c = 15; i < c; i++) {
                PopcornParticle salt = new PopcornParticle(entity.worldObj, result.x, result.y, result.z, 0.1D);
                (Minecraft.getMinecraft()).effectRenderer.addEffect((EntityFX) salt);
            }
        }
    }

    public void startAnimation(AnimatorEmoticonsController animator) {
        ((AnimationMeshConfig) animator.userConfig.meshes.get("popcorn")).visible = true;
    }

    public void stopAnimation(AnimatorEmoticonsController animator) {
        ((AnimationMeshConfig) animator.userConfig.meshes.get("popcorn")).visible = false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\PopcornEmote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */