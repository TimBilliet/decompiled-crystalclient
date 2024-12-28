package com.github.lunatrius.schematica.client.world.chunk;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkProviderSchematic implements IChunkProvider {
  private final SchematicWorld world;
  
  private final Chunk emptyChunk;
  
  private final Map<Long, ChunkSchematic> chunks = new ConcurrentHashMap<>();
  
  public ChunkProviderSchematic(SchematicWorld world) {
    this.world = world;
    this.emptyChunk = (Chunk)new EmptyChunk((World)world, 0, 0);
  }
  
  public boolean chunkExists(int x, int z) {
    return (x >= 0 && z >= 0 && x < this.world.getWidth() && z < this.world.getLength());
  }
  
  public Chunk provideChunk(int x, int z) {
    if (chunkExists(x, z)) {
      long key = ChunkCoordIntPair.chunkXZ2Int(x, z);
      ChunkSchematic chunk = this.chunks.get(Long.valueOf(key));
      if (chunk == null) {
        chunk = new ChunkSchematic((World)this.world, x, z);
        this.chunks.put(Long.valueOf(key), chunk);
      } 
      return chunk;
    } 
    return this.emptyChunk;
  }

  @Override
  public Chunk provideChunk(BlockPos pos) {
    return provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
  }

//  public Chunk func_177459_a(BlockPos pos) {
//    return provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
//  }
  
  public void populate(IChunkProvider provider, int x, int z) {}

  @Override
  public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
    return false;
  }

  
  public boolean saveChunks(boolean saveExtra, IProgressUpdate progressUpdate) {
    return true;
  }
  
  public boolean unloadQueuedChunks() {
    return false;
  }
  
  public boolean canSave() {
    return false;
  }
  
  public String makeString() {
    return "SchematicChunkCache";
  }
  
  public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
    return null;
  }
  
  public BlockPos getStrongholdGen(World world, String name, BlockPos pos) {
    return null;
  }
  
  public int getLoadedChunkCount() {
    return this.world.getWidth() * this.world.getLength();
  }
  
  public void recreateStructures(Chunk chunk, int x, int z) {}
  
  public void saveExtraData() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\world\chunk\ChunkProviderSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */