package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({LayerCreeperCharge.class})
public abstract class MixinLayerCreeperCharge {
  @ModifyArg(method = {"doRenderLayer(Lnet/minecraft/entity/monster/EntityCreeper;FFFFFFF)V"}, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelCreeper;render(Lnet/minecraft/entity/Entity;FFFFFF)V")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;depthMask(Z)V"))
  private boolean fixDepth(boolean original) {
    return true;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\layers\MixinLayerCreeperCharge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */