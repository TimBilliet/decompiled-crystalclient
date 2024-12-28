package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityEnderChestRenderer.class})
public abstract class MixinTileEntityEnderChestRenderer {
  @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityEnderChest;DDDFI)V"}, cancellable = true, at = {@At("HEAD")})
  public void renderTileEntityAt(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
    NoLag instance = NoLag.getInstance();
    if (NoLag.isEnabled(instance.hideChests) || NoLag.isEnabled(instance.fasterChestRendering))
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntityEnderChestRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */