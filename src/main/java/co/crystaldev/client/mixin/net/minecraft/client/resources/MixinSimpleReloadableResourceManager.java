package co.crystaldev.client.mixin.net.minecraft.client.resources;

import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin({SimpleReloadableResourceManager.class})
public abstract class MixinSimpleReloadableResourceManager {
  @Inject(method = {"getResource"}, at = {@At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER)}, cancellable = true)
  private void getResource(ResourceLocation location, CallbackInfoReturnable<IResource> ci) throws IOException {
    if (location instanceof MultiResourceLocation)
      ci.setReturnValue(RLUtils.getStreamForMultiskin((MultiResourceLocation)location));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinSimpleReloadableResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */