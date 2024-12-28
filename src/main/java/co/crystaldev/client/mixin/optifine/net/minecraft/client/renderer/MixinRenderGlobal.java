package co.crystaldev.client.mixin.optifine.net.minecraft.client.renderer;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.util.Reflector;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Mixin({RenderGlobal.class})
public abstract class MixinRenderGlobal {
  @Shadow
  private boolean displayListEntitiesDirty;
  
  @Shadow
  @Final
  private ChunkRenderDispatcher renderDispatcher;
  
  @Shadow
  private Set<RenderChunk> chunksToUpdate;
  
  public Set<RenderChunk> chunksToResortTransparency = new LinkedHashSet<>();
  
  public Set<RenderChunk> chunksToUpdateForced = new LinkedHashSet<>();
  
  @Unique
  private int chunksToWait;
  
  @Redirect(method = {"renderSky(Lnet/minecraft/client/renderer/WorldRenderer;FZ)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderDistance:I", opcode = 180, remap = false))
  @Dynamic("OptiFine")
  private int fixDistance(RenderGlobal instance) {
    return 256;
  }
  
  @Redirect(method = {"renderSky(FI)V"}, slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I")), at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;vboEnabled:Z", ordinal = 0))
  @Dynamic("OptiFine")
  private boolean fixVbos(RenderGlobal instance) {
    return false;
  }
  
  @Inject(method = {"updateChunks"}, cancellable = true, at = {@At("HEAD")})
  public void updateChunks(long finishTimeNano, CallbackInfo ci) {
    int lazyChunkLoading = (ClientOptions.getInstance()).lazyChunkLoading;
    finishTimeNano = (long)(finishTimeNano + 1.0E8D);
    this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
    if (this.chunksToUpdateForced.size() > 0) {
      Iterator<RenderChunk> iterator1 = this.chunksToUpdateForced.iterator();
      RenderChunk renderChunk1;
      while (iterator1.hasNext() && this.renderDispatcher.updateChunkLater(renderChunk1 = iterator1.next())) {
        renderChunk1.setNeedsUpdate(false);
        iterator1.remove();
        this.chunksToUpdate.remove(renderChunk1);
        this.chunksToResortTransparency.remove(renderChunk1);
      } 
    } 
    RenderChunk renderChunk;
    Iterator<RenderChunk> iterator;
    if (this.chunksToResortTransparency.size() > 0 && (iterator = this.chunksToResortTransparency.iterator()).hasNext() && this.renderDispatcher.updateTransparencyLater(renderChunk = iterator.next()))
      iterator.remove(); 
    double d1 = 0.0D;
    int i = Reflector.Config$getUpdatesPerFrame();
    if (!this.chunksToUpdate.isEmpty()) {
      Iterator<RenderChunk> iterator1 = this.chunksToUpdate.iterator();
      while (iterator1.hasNext()) {
        if (lazyChunkLoading != 1)
          if (this.chunksToWait <= 0) {
            this.chunksToWait = lazyChunkLoading;
          } else {
            this.chunksToWait--;
            break;
          }  
        RenderChunk renderchunk1 = iterator1.next();
        boolean flag = Reflector.RenderChunk$isChunkRegionEmpty(renderchunk1);
        boolean flag1 = flag ? this.renderDispatcher.updateChunkNow(renderchunk1) : this.renderDispatcher.updateChunkLater(renderchunk1);
        if (!flag1)
          break; 
        renderchunk1.setNeedsUpdate(false);
        iterator1.remove();
        if (!flag) {
          double d0 = 2.0D * Reflector.RenderChunkUtils$getRelativeBufferSize(renderchunk1);
          d1 += d0;
          if (d1 > i)
            break; 
        } 
      } 
    } 
    ci.cancel();
  }
}
