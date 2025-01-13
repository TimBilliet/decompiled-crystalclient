package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityEnchantmentTableRenderer.class})
public abstract class MixinTileEntityEnchantmentTableRenderer {
    @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityEnchantmentTable;DDDFI)V"}, at = {@At("HEAD")}, cancellable = true)
    private void cancelRendering(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableEnchantmentTableBooks))
            ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntityEnchantmentTableRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */