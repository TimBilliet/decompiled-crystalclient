package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderLiving.class})
public abstract class MixinRenderLiving<T extends EntityLiving> {
    @Inject(method = {"shouldRender(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/client/renderer/culling/ICamera;DDD)Z"}, cancellable = true, at = {@At("HEAD")})
    public void shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> ci) {
        if (livingEntity instanceof net.minecraft.entity.monster.EntityMob && NoLag.isEnabled((NoLag.getInstance()).hideMobs) && !(livingEntity instanceof net.minecraft.entity.monster.EntityCreeper))
            ci.setReturnValue(Boolean.FALSE);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinRenderLiving.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */