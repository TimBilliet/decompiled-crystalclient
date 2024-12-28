package mapwriter.tasks;

import mapwriter.region.RegionManager;

public class CloseRegionManagerTask extends Task {
  private final RegionManager regionManager;
  
  public CloseRegionManagerTask(RegionManager regionManager) {
    this.regionManager = regionManager;
  }
  
  public void run() {
    this.regionManager.close();
  }
  
  public void onComplete() {}
  
  public boolean CheckForDuplicate() {
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\CloseRegionManagerTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */