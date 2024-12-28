package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity.layers;

import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerArmorBase.class})
public abstract class MixinLayerArmorBase {
  @Overwrite
  public boolean shouldCombineTextures() {
    return ((OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).redArmorOnHit);
  }
  
  @Inject(method = {"renderGlint"}, at = {@At("HEAD")}, cancellable = true)
  private void disableEnchantGlint(CallbackInfo ci) {
    if (NoLag.isEnabled((NoLag.getInstance()).disableEnchantmentGlint))
      ci.cancel(); 
  }
}
