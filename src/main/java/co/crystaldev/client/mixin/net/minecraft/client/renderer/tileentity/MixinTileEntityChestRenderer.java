package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityChestRenderer.class})
public abstract class MixinTileEntityChestRenderer {
  @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityChest;DDDFI)V"}, cancellable = true, at = {@At("HEAD")})
  public void renderTileEntityAt(TileEntityChest block, double x, double y, double z, float partialTicks, int destroyState, CallbackInfo ci) {
    NoLag instance = NoLag.getInstance();
    if (NoLag.isEnabled(instance.hideChests) || NoLag.isEnabled(instance.fasterChestRendering))
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntityChestRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */