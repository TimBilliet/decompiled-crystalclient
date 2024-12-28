package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = {"net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace"})
public abstract class MixinBlockModelRenderer_AmbientOcclusionFace {
  @Redirect(method = {"updateVertexBrightness(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;[FLjava/util/BitSet;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isTranslucent()Z"))
  private boolean updateSmoothLighting(Block block) {
    return (!block.isVisuallyOpaque() || block.getLightOpacity() == 0);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinBlockModelRenderer_AmbientOcclusionFace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */