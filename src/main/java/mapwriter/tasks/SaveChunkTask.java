package mapwriter.tasks;

import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaveChunkTask extends Task {
  private MwChunk chunk;
  
  private RegionManager regionManager;
  
  private final AtomicBoolean Running = new AtomicBoolean();
  
  private static final HashMap<Long, SaveChunkTask> chunksUpdating = new HashMap<>();
  
  public SaveChunkTask(MwChunk chunk, RegionManager regionManager) {
    this.chunk = chunk;
    this.regionManager = regionManager;
  }
  
  public void run() {
    this.Running.set(true);
    this.chunk.write(this.regionManager.regionFileCache);
  }
  
  public void onComplete() {
    Long coords = this.chunk.getCoordIntPair();
    chunksUpdating.remove(coords);
    this.Running.set(false);
  }
  
  public boolean CheckForDuplicate() {
    Long coords = this.chunk.getCoordIntPair();
    if (!chunksUpdating.containsKey(coords)) {
      chunksUpdating.put(coords, this);
      return false;
    } 
    SaveChunkTask task2 = chunksUpdating.get(coords);
    if (!task2.Running.get()) {
      task2.UpdateChunkData(this.chunk, this.regionManager);
    } else {
      chunksUpdating.put(coords, this);
      return false;
    } 
    return true;
  }
  
  public void UpdateChunkData(MwChunk chunk, RegionManager regionManager) {
    this.chunk = chunk;
    this.regionManager = regionManager;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\SaveChunkTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */