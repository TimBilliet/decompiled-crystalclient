package mapwriter.map;

import mapwriter.MapWriterMod;
import mapwriter.config.Config;
import mapwriter.map.mapmode.LargeMapMode;
import mapwriter.map.mapmode.MapMode;
import mapwriter.map.mapmode.SmallMapMode;

import java.util.LinkedList;
import java.util.List;

public class MiniMap {
    public MapMode smallMapMode;

    public MapMode largeMapMode;

    public MapView view;

    public MapRenderer smallMap;

    public MapRenderer largeMap;

    private final List<MapRenderer> mapList;

    public MapRenderer currentMap;

    public MiniMap(MapWriterMod mapWriterMod) {
        this.view = new MapView(mapWriterMod, false);
        this.view.setZoomLevel(Config.overlayZoomLevel);
        this.smallMap = new MapRenderer(mapWriterMod, this.smallMapMode = (MapMode) new SmallMapMode(), this.view);
        this.largeMap = new MapRenderer(mapWriterMod, this.largeMapMode = (MapMode) new LargeMapMode(), this.view);
        this.mapList = new LinkedList<>();
        this.mapList.add(this.smallMap);
        this.mapList.add(this.largeMap);
        this.mapList.add(null);
        nextOverlayMode(0);
        this.currentMap = this.mapList.get(Config.overlayModeIndex);
    }

    public void close() {
        this.mapList.clear();
        this.currentMap = null;
    }

    public void nextOverlayMode(int increment) {
        int size = this.mapList.size();
        Config.overlayModeIndex = (Config.overlayModeIndex + size + increment) % size;
        this.currentMap = this.mapList.get(Config.overlayModeIndex);
    }

    public void drawCurrentMap() {
        if (this.currentMap != null)
            this.currentMap.draw();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\MiniMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */