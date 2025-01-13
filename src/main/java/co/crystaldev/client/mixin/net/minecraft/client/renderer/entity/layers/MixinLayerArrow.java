package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity.layers;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerArrow.class})
public abstract class MixinLayerArrow {
    @Redirect(method = {"doRenderLayer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V"))
    private void removeDisable() {
    }

    @Redirect(method = {"doRenderLayer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V"))
    private void removeEnable() {
    }

    @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    private void cancelRendering(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableAttachedArrows))
            ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\layers\MixinLayerArrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */