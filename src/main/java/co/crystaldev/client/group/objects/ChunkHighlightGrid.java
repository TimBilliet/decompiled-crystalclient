package co.crystaldev.client.group.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.LinkedList;

public class ChunkHighlightGrid {
  public String toString() {
    return "ChunkHighlightGrid(highlightedChunks=" + getHighlightedChunks() + ")";
  }
  
  @SerializedName("highlightedChunks")
  private final LinkedList<ChunkHighlight> highlightedChunks = new LinkedList<>();
  
  public LinkedList<ChunkHighlight> getHighlightedChunks() {
    return this.highlightedChunks;
  }
  
  public void highlightChunk(ChunkHighlight chunk) {
    this.highlightedChunks.removeIf(c -> (c.getX() == chunk.getX() && c.getZ() == chunk.getZ() && c.getType() == chunk.getType()));
    this.highlightedChunks.add(chunk);
  }
  
  public void removeChunk(int x, int z) {
    this.highlightedChunks.removeIf(c -> (c.getX() == x && c.getZ() == z));
  }
  
  public void sort(Comparator<ChunkHighlight> comparator) {
    this.highlightedChunks.sort(comparator);
  }
  
  public boolean isEmpty() {
    return this.highlightedChunks.isEmpty();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\ChunkHighlightGrid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */