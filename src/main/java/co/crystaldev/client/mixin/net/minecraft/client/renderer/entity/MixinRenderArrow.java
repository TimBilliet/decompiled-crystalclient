package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.patcher.hook.RenderArrowHook;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderArrow.class})
public abstract class MixinRenderArrow {
  @Inject(method = {"doRender(Lnet/minecraft/entity/projectile/EntityArrow;DDDFF)V"}, at = {@At("HEAD")}, cancellable = true)
  private void cancelArrowRendering(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
    if (RenderArrowHook.cancelRendering(entity))
      ci.cancel();
  }
}