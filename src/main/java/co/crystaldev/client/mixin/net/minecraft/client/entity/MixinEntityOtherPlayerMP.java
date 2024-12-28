package co.crystaldev.client.mixin.net.minecraft.client.entity;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityOtherPlayerMP.class})
public abstract class MixinEntityOtherPlayerMP {
  @Inject(method = {"onLivingUpdate"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityOtherPlayerMP;updateArmSwingProgress()V", shift = At.Shift.AFTER)})
  private void removeUselessAnimations(CallbackInfo ci) {
    ci.cancel();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\entity\MixinEntityOtherPlayerMP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */