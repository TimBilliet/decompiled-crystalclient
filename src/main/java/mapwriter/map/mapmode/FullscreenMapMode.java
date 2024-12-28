package mapwriter.map.mapmode;

import mapwriter.config.Config;

public class FullscreenMapMode extends MapMode {
  private int blockX = -1;
  
  public int getBlockX() {
    return this.blockX;
  }
  
  private int blockY = -1;
  
  public int getBlockY() {
    return this.blockY;
  }
  
  private int blockZ = -1;
  
  public int getBlockZ() {
    return this.blockZ;
  }
  
  private int chunkX = -1;
  
  public int getChunkX() {
    return this.chunkX;
  }
  
  private int chunkZ = -1;
  
  public int getChunkZ() {
    return this.chunkZ;
  }
  
  public FullscreenMapMode() {
    super(Config.fullScreenMap);
  }
  
  public void setCoordinates(int blockX, int blockY, int blockZ) {
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
    this.chunkX = blockX >> 4;
    this.chunkZ = blockZ >> 4;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\mapmode\FullscreenMapMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */