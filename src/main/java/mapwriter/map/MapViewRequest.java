package mapwriter.map;

public class MapViewRequest {
  public final int xMin;
  
  public final int xMax;
  
  public final int zMin;
  
  public final int zMax;
  
  public final int zoomLevel;
  
  public final int dimension;
  
  public MapViewRequest(MapView view) {
    this.zoomLevel = view.getRegionZoomLevel();
    int size = 512 << this.zoomLevel;
    this.xMin = (int)view.getMinX() & -size;
    this.zMin = (int)view.getMinZ() & -size;
    this.xMax = (int)view.getMaxX() & -size;
    this.zMax = (int)view.getMaxZ() & -size;
    this.dimension = view.getDimension();
  }
  
  public boolean equals(MapViewRequest req) {
    return (req != null && req.zoomLevel == this.zoomLevel && req.dimension == this.dimension && req.xMin == this.xMin && req.xMax == this.xMax && req.zMin == this.zMin && req.zMax == this.zMax);
  }
  
  public boolean mostlyEquals(MapViewRequest req) {
    return (req != null && req.zoomLevel == this.zoomLevel && req.dimension == this.dimension);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\MapViewRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */