package co.crystaldev.client.mixin.net.minecraft.util;

import co.crystaldev.client.Client;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Timer.class})
//public abstract class MixinTimer extends Timer {
public abstract class MixinTimer{
//  public MixinTimer(float p_i1018_1_) {
//    super(p_i1018_1_);
//  }

  @Inject(method = {"<init>"}, at = {@At("RETURN")})
  private void constructorTail(float tps, CallbackInfo ci) {
    Client.setTimer((Timer)(Object)this);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraf\\util\MixinTimer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */