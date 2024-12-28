package mapwriter.map;

import mapwriter.BackgroundExecutor;
import mapwriter.region.Region;
import mapwriter.region.RegionManager;
import mapwriter.tasks.MapUpdateViewTask;
import mapwriter.tasks.Task;
import mapwriter.util.Texture;

import java.util.ArrayList;
import java.util.List;

public class MapTexture extends Texture {
  public int textureRegions;
  
  public int textureSize;
  
  private MapViewRequest loadedView = null;
  
  private MapViewRequest requestedView = null;
  
  private final Region[] regionArray;
  
  private static class Rect {
    final int x;
    
    final int y;
    
    final int w;
    
    final int h;
    
    Rect(int x, int y, int w, int h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }
  }
  
  private final List<Rect> textureUpdateQueue = new ArrayList<>();
  
  public MapTexture(int textureSize, boolean linearScaling) {
    super(textureSize, textureSize, 0, 9729, 9729, 10497);
    setLinearScaling(linearScaling);
    this.textureRegions = textureSize >> 9;
    this.textureSize = textureSize;
    this.regionArray = new Region[this.textureRegions * this.textureRegions];
  }
  
  public void requestView(MapViewRequest req, BackgroundExecutor executor, RegionManager regionManager) {
    if (this.requestedView == null || !this.requestedView.equals(req)) {
      this.requestedView = req;
      executor.addTask((Task)new MapUpdateViewTask(this, regionManager, req));
    } 
  }
  
  public void processTextureUpdates() {
    synchronized (this.textureUpdateQueue) {
      for (Rect rect : this.textureUpdateQueue)
        updateTextureArea(rect.x, rect.y, rect.w, rect.h); 
      this.textureUpdateQueue.clear();
    } 
  }
  
  public void setLoaded(MapViewRequest req) {
    this.loadedView = req;
  }
  
  public boolean isLoaded(MapViewRequest req) {
    return (this.loadedView != null && this.loadedView.mostlyEquals(req));
  }
  
  public synchronized void setRGBOpaque(int x, int y, int w, int h, int[] pixels, int offset, int scanSize) {
    int bufOffset = y * this.w + x;
    for (int i = 0; i < h; i++) {
      setPixelBufPosition(bufOffset + i * this.w);
      int rowOffset = offset + i * scanSize;
      for (int j = 0; j < w; j++) {
        int colour = pixels[rowOffset + j];
        if (colour != 0)
          colour |= 0xFF000000; 
        pixelBufPut(colour);
      } 
    } 
  }
  
  public void addTextureUpdate(int x, int z, int w, int h) {
    synchronized (this.textureUpdateQueue) {
      this.textureUpdateQueue.add(new Rect(x, z, w, h));
    } 
  }
  
  public void updateTextureFromRegion(Region region, int x, int z, int w, int h) {
    int tx = x >> region.zoomLevel & this.w - 1;
    int ty = z >> region.zoomLevel & this.h - 1;
    int tw = w >> region.zoomLevel;
    int th = h >> region.zoomLevel;
    tw = Math.min(tw, this.w - tx);
    th = Math.min(th, this.h - th);
    int[] pixels = region.getPixels();
    if (pixels != null) {
      setRGBOpaque(tx, ty, tw, th, pixels, region.getPixelOffset(x, z), 512);
    } else {
      fillRect(tx, ty, tw, th, 0);
    } 
    addTextureUpdate(tx, ty, tw, th);
  }
  
  public int getRegionIndex(int x, int z, int zoomLevel) {
    x = x >> 9 + zoomLevel & this.textureRegions - 1;
    z = z >> 9 + zoomLevel & this.textureRegions - 1;
    return z * this.textureRegions + x;
  }
  
  public void loadRegion(RegionManager regionManager, int x, int z, int zoomLevel, int dimension) {
    int index = getRegionIndex(x, z, zoomLevel);
    Region currentRegion = this.regionArray[index];
    if (currentRegion == null || !currentRegion.equals(x, z, zoomLevel, dimension)) {
      Region newRegion = regionManager.getRegion(x, z, zoomLevel, dimension);
      this.regionArray[index] = newRegion;
      updateTextureFromRegion(newRegion, newRegion.x, newRegion.z, newRegion.size, newRegion.size);
    } 
  }
  
  public void loadRegions(RegionManager regionManager, MapViewRequest req) {
    int size = 512 << req.zoomLevel;
    int z;
    for (z = req.zMin; z <= req.zMax; z += size) {
      int x;
      for (x = req.xMin; x <= req.xMax; x += size)
        loadRegion(regionManager, x, z, req.zoomLevel, req.dimension); 
    } 
  }
  
  public void updateArea(RegionManager regionManager, int x, int z, int w, int h, int dimension) {
    for (Region region : this.regionArray) {
      if (region != null && region.isAreaWithin(x, z, w, h, dimension))
        updateTextureFromRegion(region, x, z, w, h); 
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\MapTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */