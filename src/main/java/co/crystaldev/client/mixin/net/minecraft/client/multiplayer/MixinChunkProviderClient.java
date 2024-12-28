package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import co.crystaldev.client.event.impl.world.ChunkEvent;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ChunkProviderClient.class})
public abstract class MixinChunkProviderClient {
  @Inject(method = {"loadChunk"}, locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = {@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER)})
  private void loadChunk(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir, Chunk chunk) {
    (new ChunkEvent.Load(chunk)).call();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\multiplayer\MixinChunkProviderClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */