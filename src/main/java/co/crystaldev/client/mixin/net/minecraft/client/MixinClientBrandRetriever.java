package co.crystaldev.client.mixin.net.minecraft.client;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientBrandRetriever.class})
public abstract class MixinClientBrandRetriever {
  @Unique
  private static final String BRANDING = String.format("%s-v%s", "crystalclient", "1.1.16-projectassfucker");
  
  @Inject(method = {"getClientModName"}, cancellable = true, at = {@At("HEAD")})
  private static void getClientModName(CallbackInfoReturnable<String> cir) {
    cir.setReturnValue(BRANDING);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\MixinClientBrandRetriever.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */