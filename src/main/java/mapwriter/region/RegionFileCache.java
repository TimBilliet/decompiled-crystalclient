package mapwriter.region;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegionFileCache {
  class LruCache extends LinkedHashMap<String, RegionFile> {
    private static final long serialVersionUID = 1L;
    
    static final int MAX_REGION_FILES_OPEN = 8;
    
    public LruCache() {
      super(16, 0.5F, true);
    }
    
    protected boolean removeEldestEntry(Map.Entry<String, RegionFile> entry) {
      boolean ret = false;
      if (size() > 8) {
        RegionFile regionFile = entry.getValue();
        regionFile.close();
        ret = true;
      } 
      return ret;
    }
  }
  
  private final LruCache regionFileCache = new LruCache();
  
  private final File worldDir;
  
  public RegionFileCache(File worldDir) {
    this.worldDir = worldDir;
  }
  
  public void close() {
    for (RegionFile regionFile : this.regionFileCache.values())
      regionFile.close(); 
    this.regionFileCache.clear();
  }
  
  public File getRegionFilePath(int x, int z, int dimension) {
    File dir = this.worldDir;
    if (dimension != 0)
      dir = new File(dir, "DIM" + dimension); 
    dir = new File(dir, "region");
    String filename = String.format("r.%d.%d.mca", new Object[] { Integer.valueOf(x >> 9), Integer.valueOf(z >> 9) });
    return new File(dir, filename);
  }
  
  public boolean regionFileExists(int x, int z, int dimension) {
    File regionFilePath = getRegionFilePath(x, z, dimension);
    return regionFilePath.isFile();
  }
  
  public RegionFile getRegionFile(int x, int z, int dimension) {
    File regionFilePath = getRegionFilePath(x, z, dimension);
    String key = regionFilePath.toString();
    RegionFile regionFile = this.regionFileCache.get(key);
    if (regionFile == null) {
      regionFile = new RegionFile(regionFilePath);
      this.regionFileCache.put(key, regionFile);
    } 
    return regionFile;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\RegionFileCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */