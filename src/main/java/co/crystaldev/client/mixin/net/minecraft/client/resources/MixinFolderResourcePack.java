package co.crystaldev.client.mixin.net.minecraft.client.resources;

import net.minecraft.client.resources.FolderResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin({FolderResourcePack.class})
public abstract class MixinFolderResourcePack {
  @Inject(method = {"getInputStreamByName"}, cancellable = true, at = {@At("HEAD")})
  private void getInputStreamByName(String name, CallbackInfoReturnable<InputStream> ci) {
    if (name.contains("crystalclient") && !((Object)this instanceof co.crystaldev.client.cosmetic.CosmeticResourcePack))
      ci.setReturnValue(null);
  }
  
  @Inject(method = {"hasResourceName"}, cancellable = true, at = {@At("HEAD")})
  private void hasResourceName(String name, CallbackInfoReturnable<Boolean> ci) {
    if (name.contains("crystalclient") && !((Object)this instanceof co.crystaldev.client.cosmetic.CosmeticResourcePack))
      ci.setReturnValue(Boolean.FALSE);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinFolderResourcePack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */