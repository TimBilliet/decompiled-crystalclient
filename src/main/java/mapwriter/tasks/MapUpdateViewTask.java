package mapwriter.tasks;

import mapwriter.map.MapTexture;
import mapwriter.map.MapViewRequest;
import mapwriter.region.RegionManager;

public class MapUpdateViewTask extends Task {
    final MapViewRequest req;

    RegionManager regionManager;

    MapTexture mapTexture;

    public MapUpdateViewTask(MapTexture mapTexture, RegionManager regionManager, MapViewRequest req) {
        this.mapTexture = mapTexture;
        this.regionManager = regionManager;
        this.req = req;
    }

    public void run() {
        this.mapTexture.loadRegions(this.regionManager, this.req);
    }

    public void onComplete() {
        this.mapTexture.setLoaded(this.req);
    }

    public boolean CheckForDuplicate() {
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\MapUpdateViewTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */