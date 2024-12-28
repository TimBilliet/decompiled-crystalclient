package co.crystaldev.client.mixin.net.minecraft.client.renderer.chunk;

import com.github.lunatrius.schematica.client.renderer.SchematicRenderCache;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderChunk.class})
public abstract class MixinRenderChunk {
  @Inject(method = {"preRenderBlocks"}, cancellable = true, at = {@At("HEAD")})
  public void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos, CallbackInfo ci) {
    if ((RenderChunk)(Object)this instanceof com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay) {
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
      ci.cancel();
    }
  }

  @Redirect(method = {"*"}, at = @At(value = "NEW", target = "net/minecraft/client/renderer/RegionRenderCache"))
  @Dynamic
  public RegionRenderCache rebuildChunk(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
    if ((RenderChunk)(Object)this instanceof com.github.lunatrius.schematica.client.renderer.chunk.proxy.SchematicRenderChunkList)
      return (RegionRenderCache)new SchematicRenderCache(worldIn, posFromIn, posToIn, subIn);
    return new RegionRenderCache(worldIn, posFromIn, posToIn, 1);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\chunk\MixinRenderChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */