package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.event.impl.render.RenderPlayerEvent;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.entity.MixinRendererLivingEntity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderPlayer.class})
public abstract class MixinRenderPlayer {
    @Inject(method = {"doRender"}, cancellable = true, at = {@At("HEAD")})
    public void doRenderPre(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        RenderPlayerEvent.Pre event = new RenderPlayerEvent.Pre((EntityPlayer) entity, (RenderPlayer) (MixinRendererLivingEntity) this, partialTicks, x, y, z);
        event.call();
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = {"doRender"}, at = {@At("TAIL")})
    public void doRenderPost(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        (new RenderPlayerEvent.Post((EntityPlayer) entity, (RenderPlayer) (MixinRendererLivingEntity) this, partialTicks, x, y, z)).call();
    }
}
