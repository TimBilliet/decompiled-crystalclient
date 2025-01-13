package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.entity.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderItem.class})
public abstract class MixinRenderItem {
    @Inject(method = {"renderEffect"}, at = {@At("HEAD")}, cancellable = true)
    private void disableEnchantGlint(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableEnchantmentGlint))
            ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinRenderItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */