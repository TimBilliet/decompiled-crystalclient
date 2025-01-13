package mchorse.emoticons.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.tick.PlayerTickEvent;
import mchorse.emoticons.cosmetic.emote.IUserEmoteData;
import mchorse.emoticons.cosmetic.emote.UserEmoticonData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class UserUpdateHandler {
    @SubscribeEvent
    public void onUpdateEntity(PlayerTickEvent.Post event) {
        EntityPlayer player = event.player;
        IUserEmoteData cap = UserEmoticonData.get((Entity) player);
        if (cap != null)
            cap.update((EntityLivingBase) player);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\client\UserUpdateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */