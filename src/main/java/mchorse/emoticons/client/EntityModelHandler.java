package mchorse.emoticons.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.render.RenderPlayerEvent;
import mchorse.emoticons.cosmetic.emote.IUserEmoteData;
import mchorse.emoticons.cosmetic.emote.UserEmoticonData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityModelHandler {
  @SubscribeEvent(priority = 0)
  public void onRenderPlayer(RenderPlayerEvent.Pre event) {
    EntityPlayer player = event.player;
    if (player.isSpectator())
      return; 
    IUserEmoteData cap = UserEmoticonData.get((Entity)player);
    if (cap != null && cap.render((EntityLivingBase)player, event.x, event.y, event.z, event.partialTicks))
      event.setCancelled(true); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\client\EntityModelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */