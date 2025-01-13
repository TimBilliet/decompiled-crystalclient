package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.particle.EffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EffectRenderer.class})
public abstract class MixinEffectRenderer {
    @Inject(method = {"addBlockDestroyEffects", "addBlockHitEffects"}, at = {@At("HEAD")}, cancellable = true)
    private void removeBlockBreakParticles(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableBlockBreakParticles))
            ci.cancel();
    }

    @ModifyConstant(method = {"addEffect"}, constant = {@Constant(intValue = 4000)})
    private int injectMaxDisplayedParticleLimit(int original) {
        return (NoLag.getInstance()).maxDisplayedParticleLimit;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinEffectRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */