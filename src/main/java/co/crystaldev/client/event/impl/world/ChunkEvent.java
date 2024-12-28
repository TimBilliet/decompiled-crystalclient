package co.crystaldev.client.event.impl.world;

import net.minecraft.world.chunk.Chunk;

public class ChunkEvent extends WorldEvent {
  private final Chunk chunk;
  
  public ChunkEvent(Chunk chunk) {
    super(chunk.getWorld());
    this.chunk = chunk;
  }
  
  public Chunk getChunk() {
    return this.chunk;
  }
  
  public static class Load extends ChunkEvent {
    public Load(Chunk chunk) {
      super(chunk);
    }
  }
  
  public static class Unload extends ChunkEvent {
    public Unload(Chunk chunk) {
      super(chunk);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\world\ChunkEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */