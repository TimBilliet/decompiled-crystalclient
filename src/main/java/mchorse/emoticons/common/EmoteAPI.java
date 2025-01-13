package mchorse.emoticons.common;

import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.cosmetic.emote.IUserEmoteData;
import mchorse.emoticons.cosmetic.emote.UserEmoticonData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteAPI {
    public static void setEmoteClient(String emote, EntityPlayer player) {
        IUserEmoteData cap = UserEmoticonData.get((Entity) player);
        if (cap != null)
            cap.setEmote(Emotes.get(emote), (EntityLivingBase) player);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\EmoteAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */