package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderEntityItem.class})
public abstract class MixinRenderEntityItem {
  @Inject(method = {"func_177078_a"}, at = {@At("HEAD")}, cancellable = true)//getModelCount
  private void disableStackedItems(CallbackInfoReturnable<Integer> cir) {
    if (NoLag.isEnabled((NoLag.getInstance()).disableStackedItems))
      cir.setReturnValue(1);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinRenderEntityItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */