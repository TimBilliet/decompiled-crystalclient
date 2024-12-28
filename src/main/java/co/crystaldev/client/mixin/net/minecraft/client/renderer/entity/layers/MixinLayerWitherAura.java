package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerWitherAura.class})
public abstract class MixinLayerWitherAura {
  @Inject(method = {"doRenderLayer(Lnet/minecraft/entity/boss/EntityWither;FFFFFFF)V"}, at = {@At("TAIL")})
  private void fixDepth(CallbackInfo ci) {
    GlStateManager.depthMask(true);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\layers\MixinLayerWitherAura.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */