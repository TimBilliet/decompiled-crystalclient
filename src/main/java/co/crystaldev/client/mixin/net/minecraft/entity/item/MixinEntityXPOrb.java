package co.crystaldev.client.mixin.net.minecraft.entity.item;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityXPOrb.class})
public abstract class MixinEntityXPOrb {
  @Redirect(method = {"onUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getEyeHeight()F"))
  private float fixOrbHeight(EntityPlayer entityPlayer) {
    return (float)(entityPlayer.getEyeHeight() / 2.0D);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\entity\item\MixinEntityXPOrb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */