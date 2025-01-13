package co.crystaldev.client.mixin.net.minecraft.client.entity;

import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer implements AbstractClientPlayerExt {
    @Unique
    private CosmeticPlayer crystal$cosmeticPlayer;

    @Inject(method = {"getLocationCape"}, cancellable = true, at = {@At("HEAD")})
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> ci) {
        ensureCosmeticPlayer();
        if (this.crystal$cosmeticPlayer != null && (this.crystal$cosmeticPlayer.hasCloak() || this.crystal$cosmeticPlayer.isShouldHideLegacyCosmetics()))
            ci.setReturnValue(null);
    }

    public CosmeticPlayer crystal$getCosmeticPlayer() {
        ensureCosmeticPlayer();
        return this.crystal$cosmeticPlayer;
    }

    private void ensureCosmeticPlayer() {
        if (this.crystal$cosmeticPlayer == null)
            this.crystal$cosmeticPlayer = CosmeticCache.getInstance().fromPlayer((EntityPlayer) (Object) this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\entity\MixinAbstractClientPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */