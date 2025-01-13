package co.crystaldev.client.mixin.net.minecraft.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MobSpawnerBaseLogic.class})
public abstract class MixinMobSpawnerBaseLogic {
    @Inject(method = {"updateSpawner"}, cancellable = true, at = {@At("HEAD")})
    public void updateSpawner(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableSpawnerAnimation))
            ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\tileentity\MixinMobSpawnerBaseLogic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */