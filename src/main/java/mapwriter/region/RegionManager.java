package mapwriter.region;

import co.crystaldev.client.Reference;
import mapwriter.util.Logging;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegionManager {
    private final LruCache regionMap;

    public final File worldDir;

    public final File imageDir;

    public BlockColors blockColours;

    static class LruCache extends LinkedHashMap<Long, Region> {
        private static final long serialVersionUID = 1L;

        private static final int MAX_LOADED_REGIONS = 64;

        public LruCache() {
            super(128, 0.5F, true);
        }

        protected boolean removeEldestEntry(Map.Entry<Long, Region> entry) {
            boolean ret = false;
            if (size() > 64) {
                Region region = entry.getValue();
                region.close();
                ret = true;
            }
            return ret;
        }
    }

    public static Logger logger = Reference.LOGGER;

    public final RegionFileCache regionFileCache;

    public int maxZoom;

    public int minZoom;

    public RegionManager(File worldDir, File imageDir, BlockColors blockColours, int minZoom, int maxZoom) {
        this.worldDir = worldDir;
        this.imageDir = imageDir;
        this.blockColours = blockColours;
        this.regionMap = new LruCache();
        this.regionFileCache = new RegionFileCache(worldDir);
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public void close() {
        for (Region region : this.regionMap.values()) {
            if (region != null)
                region.close();
        }
        this.regionMap.clear();
        this.regionFileCache.close();
    }

    private static int incrStatsCounter(Map<String, Integer> h, String key) {
        int n = 1;
        if (h.containsKey(key))
            n = (Integer) h.get(key) + 1;
        h.put(key, n);
        return n;
    }

    public Region getRegion(int x, int z, int zoomLevel, int dimension) {
        Region region = this.regionMap.get(Region.getKey(x, z, zoomLevel, dimension));
        if (region == null) {
            region = new Region(this, x, z, zoomLevel, dimension);
            this.regionMap.put(region.key, region);
        }
        return region;
    }

    public void updateChunk(MwChunk chunk) {
        Region region = getRegion(chunk.x << 4, chunk.z << 4, 0, chunk.dimension);
        region.updateChunk(chunk);
    }

    public void rebuildRegions(int xStart, int zStart, int w, int h, int dimension) {
        xStart &= 0xFFFFFE00;
        zStart &= 0xFFFFFE00;
        w = w + 512 & 0xFFFFFE00;
        h = h + 512 & 0xFFFFFE00;
        Logging.logInfo("rebuilding regions from (%d, %d) to (%d, %d)", new Object[]{Integer.valueOf(xStart), Integer.valueOf(zStart), Integer.valueOf(xStart + w), Integer.valueOf(zStart + h)});
        for (int rX = xStart; rX < xStart + w; rX += 512) {
            for (int rZ = zStart; rZ < zStart + h; rZ += 512) {
                Region region = getRegion(rX, rZ, 0, dimension);
                if (this.regionFileCache.regionFileExists(rX, rZ, dimension)) {
                    region.clear();
                    for (int cz = 0; cz < 32; cz++) {
                        for (int cx = 0; cx < 32; cx++) {
                            MwChunk chunk = MwChunk.read((region.x >> 4) + cx, (region.z >> 4) + cz, region.dimension, this.regionFileCache);
                            region.updateChunk(chunk);
                        }
                    }
                }
                region.updateZoomLevels();
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\RegionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */