package mchorse.emoticons.cosmetic.emote;

import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.entity.EntityLivingBase;

public interface IUserEmoteData {
  void setEmote(Emote paramEmote, EntityLivingBase paramEntityLivingBase);
  
  Emote getEmote();
  
  void update(EntityLivingBase paramEntityLivingBase);
  
  boolean render(EntityLivingBase paramEntityLivingBase, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\cosmetic\emote\IUserEmoteData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */