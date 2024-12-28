package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.EntityLivingBase;

import java.util.Random;

public class Emote {
  public final String name;
  
  public int duration;
  
  public boolean looping;
  
  public String sound;
  
  public Random rand = new Random();
  
  public Emote(String name, int duration, boolean looping, String sound) {
    this.name = name;
    this.duration = duration;
    this.looping = looping;
    this.sound = sound;
  }
  
  public void progressAnimation(EntityLivingBase entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {}
  
  public void startAnimation(AnimatorEmoticonsController animator) {}
  
  public void stopAnimation(AnimatorEmoticonsController animator) {}
  
  public Emote getDynamicEmote() {
    return this;
  }
  
  public Emote getDynamicEmote(String suffix) {
    return this;
  }
  
  public String getKey() {
    return this.name;
  }
  
  public float rand(float factor) {
    return this.rand.nextFloat() * factor - factor / 2.0F;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\Emote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */