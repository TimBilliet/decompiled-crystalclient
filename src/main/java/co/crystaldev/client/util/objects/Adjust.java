package co.crystaldev.client.util.objects;

import net.minecraft.util.BlockPos;

public class Adjust {
  public BlockPos origin;
  
  public BlockPos finish;
  
  public int patches;
  
  public double patchIndex;
  
  public String coordText;
  
  public Adjust(BlockPos origin, BlockPos finish, int patches, double patchIndex, String coordText) {
    this.origin = origin;
    this.finish = finish;
    this.patches = patches;
    this.patchIndex = patchIndex;
    this.coordText = coordText;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Adjust.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */