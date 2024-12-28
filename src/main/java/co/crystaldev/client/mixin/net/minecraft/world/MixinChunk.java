package co.crystaldev.client.mixin.net.minecraft.world;

import co.crystaldev.client.event.impl.world.ChunkEvent;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Chunk.class})
public abstract class MixinChunk {
  @Inject(method = {"onChunkLoad"}, at = {@At("TAIL")})
  private void onChunkLoad(CallbackInfo ci) {
    (new ChunkEvent.Load((Chunk)(Object)this)).call();
  }
  
  @Inject(method = {"onChunkUnload"}, at = {@At("TAIL")})
  private void onChunkUnload(CallbackInfo ci) {
    (new ChunkEvent.Unload((Chunk)(Object)this)).call();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\world\MixinChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */