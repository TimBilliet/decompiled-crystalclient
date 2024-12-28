package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ViewFrustum.class})
public interface MixinViewFrustum {
  @Invoker("getRenderChunk")
  RenderChunk callGetRenderChunk(BlockPos paramBlockPos);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\MixinViewFrustum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */