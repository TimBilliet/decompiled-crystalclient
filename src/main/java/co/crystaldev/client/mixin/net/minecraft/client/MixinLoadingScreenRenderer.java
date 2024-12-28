package co.crystaldev.client.mixin.net.minecraft.client;

import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LoadingScreenRenderer.class})
public abstract class MixinLoadingScreenRenderer {
  @Inject(method = {"setLoadingProgress"}, at = {@At("HEAD")}, cancellable = true)
  private void skipProgressIfNegative(int progress, CallbackInfo ci) {
    if (progress < 0)
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\MixinLoadingScreenRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */