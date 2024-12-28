package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ChunkRenderDispatcher.class})
public interface MixinChunkRenderDispatcher {
  @Invoker("uploadDisplayList")
  void callUploadDisplayList(WorldRenderer paramWorldRenderer, int paramInt, RenderChunk paramRenderChunk);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\chunk\MixinChunkRenderDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */