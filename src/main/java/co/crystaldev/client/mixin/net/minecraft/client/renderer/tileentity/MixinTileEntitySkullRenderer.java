package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntitySkullRenderer.class})
public abstract class MixinTileEntitySkullRenderer {
  @Inject(method = {"renderSkull"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V")})
  private void enableBlending(CallbackInfo ci) {
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
  }
  
  @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySkull;DDDFI)V"}, at = {@At("HEAD")}, cancellable = true)
  private void cancelRendering(CallbackInfo ci) {
    if (NoLag.isEnabled((NoLag.getInstance()).disableSkulls))
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntitySkullRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */