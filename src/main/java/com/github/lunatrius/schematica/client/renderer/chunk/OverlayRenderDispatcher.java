package com.github.lunatrius.schematica.client.renderer.chunk;

import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.chunk.MixinChunkRenderDispatcher;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;

public class OverlayRenderDispatcher extends ChunkRenderDispatcher {
  public ListenableFuture<Object> func_178503_a(EnumWorldBlockLayer layer, WorldRenderer worldRenderer, RenderChunk renderChunk, CompiledChunk compiledChunk) {
    if (!Minecraft.getMinecraft().isCallingFromMinecraftThread() || OpenGlHelper.useVbo())
      return super.uploadChunk(layer, worldRenderer, renderChunk, compiledChunk);
    ((MixinChunkRenderDispatcher)this).callUploadDisplayList(worldRenderer, ((RenderOverlayList)renderChunk).getDisplayList(layer, compiledChunk), renderChunk);
    worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
    return Futures.immediateFuture(null);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\OverlayRenderDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */