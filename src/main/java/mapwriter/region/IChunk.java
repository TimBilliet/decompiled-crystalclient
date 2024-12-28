package mapwriter.region;

public interface IChunk {
  int getBlockAndMetadata(int paramInt1, int paramInt2, int paramInt3);
  
  int getBiome(int paramInt1, int paramInt2);
  
  int getLightValue(int paramInt1, int paramInt2, int paramInt3);
  
  int getMaxY();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\IChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */