package mapwriter.region;

import java.io.File;

public class Region {
  public RegionManager regionManager;
  
  public static final int SHIFT = 9;
  
  public static final int SIZE = 512;
  
  public static final int MASK = -512;
  
  public final int x;
  
  public final int z;
  
  public final int dimension;
  
  public final int zoomLevel;
  
  public final Long key;
  
  public final int size;
  
  public SurfacePixels surfacePixels;
  
  public Region(RegionManager regionManager, int x, int z, int zoomLevel, int dimension) {
    this.regionManager = regionManager;
    this.zoomLevel = Math.min(Math.max(0, zoomLevel), regionManager.maxZoom);
    this.dimension = dimension;
    this.size = 512 << zoomLevel;
    this.x = x & -this.size;
    this.z = z & -this.size;
    this.key = getKey(this.x, this.z, this.zoomLevel, this.dimension);
    File surfaceImageFile = getImageFile();
    this.surfacePixels = new SurfacePixels(this, surfaceImageFile);
  }
  
  public void close() {
    this.surfacePixels.close();
  }
  
  public void clear() {
    this.surfacePixels.clear();
  }
  
  public String toString() {
    return String.format("(%d,%d) z%d dim%d", new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.z), Integer.valueOf(this.zoomLevel), Integer.valueOf(this.dimension) });
  }
  
  private static File addDimensionDirToPath(File dir, int dimension) {
    if (dimension != 0)
      dir = new File(dir, "DIM" + dimension); 
    return dir;
  }
  
  public File getImageFile() {
    File dimDir = addDimensionDirToPath(this.regionManager.imageDir, this.dimension);
    File zoomDir = new File(dimDir, "z" + this.zoomLevel);
    zoomDir.mkdirs();
    String filename = String.format("%d.%d.png", new Object[] { Integer.valueOf(this.x >> 9 + this.zoomLevel), Integer.valueOf(this.z >> 9 + this.zoomLevel) });
    return new File(zoomDir, filename);
  }
  
  public boolean equals(int x, int z, int zoomLevel, int dimension) {
    x &= -this.size;
    z &= -this.size;
    return (this.x == x && this.z == z && this.zoomLevel == zoomLevel && this.dimension == dimension);
  }
  
  public boolean equals(Object o) {
    boolean equal = false;
    if (o != null && 
      o instanceof Region) {
      Region region = (Region)o;
      equal = equals(region.x, region.z, region.zoomLevel, region.dimension);
    } 
    return equal;
  }
  
  public static Long getKey(int x, int z, int zoomLevel, int dimension) {
    x = x >> 9 + zoomLevel & 0xFFFF;
    z = z >> 9 + zoomLevel & 0xFFFF;
    zoomLevel &= 0xFF;
    dimension &= 0xFF;
    return (long) (dimension << 40L | zoomLevel << 32L | z << 16L | x);
  }
  
  public int getPixelOffset(int x, int z) {
    return ((z >> this.zoomLevel & 0x1FF) << 9) + (x >> this.zoomLevel & 0x1FF);
  }
  
  public int[] getPixels() {
    return this.surfacePixels.getPixels();
  }
  
  public boolean isAreaWithin(int x, int z, int w, int h, int dimension) {
    return (x >= this.x && z >= this.z && x + w <= this.x + this.size && z + h <= this.z + this.size && dimension == this.dimension);
  }
  
  public Region updateNextZoomLevel(int x, int z, int w, int h) {
    int[] srcPixels = this.surfacePixels.getPixels();
    Region dstRegion = null;
    if (srcPixels != null) {
      int dstZoomLevel = this.zoomLevel + 1;
      if (dstZoomLevel <= this.regionManager.maxZoom) {
        dstRegion = this.regionManager.getRegion(x, z, dstZoomLevel, this.dimension);
        int dstW = Math.max(1, w >> dstRegion.zoomLevel);
        int dstH = Math.max(1, h >> dstRegion.zoomLevel);
        int srcX = x >> this.zoomLevel & 0x1FF & 0xFFFFFFFE;
        int srcZ = z >> this.zoomLevel & 0x1FF & 0xFFFFFFFE;
        int dstX = x >> dstRegion.zoomLevel & 0x1FF;
        int dstZ = z >> dstRegion.zoomLevel & 0x1FF;
        dstRegion.surfacePixels.updateScaled(srcPixels, srcX, srcZ, dstX, dstZ, dstW, dstH);
      } 
    } 
    return dstRegion;
  }
  
  public void updateZoomLevels(int x, int z, int w, int h) {
    Region nextRegion = this;
    while (nextRegion != null)
      nextRegion = nextRegion.updateNextZoomLevel(x, z, w, h); 
  }
  
  public void updateZoomLevels() {
    updateZoomLevels(this.x, this.z, this.size, this.size);
  }
  
  public void updateChunk(MwChunk chunk) {
    if (this.zoomLevel == 0)
      this.surfacePixels.updateChunk(chunk); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\Region.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */