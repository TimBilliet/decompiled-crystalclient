package mapwriter.map;

import co.crystaldev.client.feature.impl.hud.MapWriter;
import mapwriter.MapWriterMod;
import mapwriter.api.MwAPI;
import mapwriter.config.Config;
import mapwriter.map.mapmode.MapMode;

import java.util.List;

public class MapView {
  private int zoomLevel = 0;
  
  private int dimension = 0;
  
  private int textureSize = 2048;
  
  public double x = 0.0D;
  
  public double getX() {
    return this.x;
  }
  
  public double z = 0.0D;
  
  public double getZ() {
    return this.z;
  }
  
  public int mapW = 0;
  
  public int mapH = 0;
  
  public int baseW = 1;
  
  public int baseH = 1;
  
  private double width = 1.0D;
  
  public double getWidth() {
    return this.width;
  }
  
  private double height = 1.0D;
  
  private final int minZoom;
  
  private final int maxZoom;
  
  private final boolean fullscreenMap;
  
  public double getHeight() {
    return this.height;
  }
  
  public MapView(MapWriterMod mapWriterMod, boolean fullscreenMap) {
    this.minZoom = (MapWriter.getInstance()).zoomInLevels;
    this.maxZoom = (MapWriter.getInstance()).zoomOutLevels;
    this.fullscreenMap = fullscreenMap;
    if (this.fullscreenMap)
      setZoomLevel(Config.fullScreenZoomLevel); 
    setZoomLevel(Config.overlayZoomLevel);
    setViewCentre(mapWriterMod.playerX, mapWriterMod.playerZ);
  }
  
  public void setViewCentre(double blockX, double blockZ) {
    this.x = blockX;
    this.z = blockZ;
    MwAPI.getEnabledDataProviders().forEach(provider -> provider.onMapCenterChanged(blockX, blockZ, this));
  }
  
  public void panView(double relX, double relZ) {
    setViewCentre(this.x + relX * this.width, this.z + relZ * this.height);
  }
  
  public int setZoomLevel(int zoomLevel) {
    int prevZoomLevel = this.zoomLevel;
    this.zoomLevel = Math.min(Math.max(this.minZoom, zoomLevel), this.maxZoom);
    if (prevZoomLevel != this.zoomLevel)
      updateZoom(); 
    if (this.fullscreenMap)
      Config.fullScreenZoomLevel = this.zoomLevel; 
    Config.overlayZoomLevel = this.zoomLevel;
    return this.zoomLevel;
  }
  
  private void updateZoom() {
    if (this.zoomLevel >= 0) {
      this.width = (this.baseW << this.zoomLevel);
      this.height = (this.baseH << this.zoomLevel);
    } else {
      this.width = (this.baseW >> -this.zoomLevel);
      this.height = (this.baseH >> -this.zoomLevel);
    } 
    MwAPI.getEnabledDataProviders().forEach(provider -> provider.onZoomChanged(getZoomLevel(), this));
  }
  
  public void adjustZoomLevel(int n) {
    setZoomLevel(this.zoomLevel + n);
  }
  
  public int getZoomLevel() {
    return this.zoomLevel;
  }
  
  public int getRegionZoomLevel() {
    return Math.max(0, this.zoomLevel);
  }
  
  public void zoomToPoint(int newZoomLevel, double bX, double bZ) {
    int prevZoomLevel = this.zoomLevel;
    newZoomLevel = setZoomLevel(newZoomLevel);
    double zF = Math.pow(2.0D, (newZoomLevel - prevZoomLevel));
    setViewCentre(bX - (bX - this.x) * zF, bZ - (bZ - this.z) * zF);
  }
  
  public void setDimension(int dimension) {
    double scale = 1.0D;
    if (dimension != this.dimension) {
      if (this.dimension != -1 && dimension == -1) {
        scale = 0.125D;
      } else if (this.dimension == -1) {
        scale = 8.0D;
      } 
      this.dimension = dimension;
      setViewCentre(this.x * scale, this.z * scale);
    } 
    MwAPI.getEnabledDataProviders().forEach(provider -> provider.onDimensionChanged(this.dimension, this));
  }
  
  public void setDimensionAndAdjustZoom(int dimension) {
    int zoomLevelChange = 0;
    if (this.dimension != -1 && dimension == -1) {
      zoomLevelChange = -3;
    } else if (this.dimension == -1 && dimension != -1) {
      zoomLevelChange = 3;
    } 
    setZoomLevel(getZoomLevel() + zoomLevelChange);
    setDimension(dimension);
  }
  
  public void nextDimension(List<Integer> dimensionList, int n) {
    int i = dimensionList.indexOf(Integer.valueOf(this.dimension));
    i = Math.max(0, i);
    int size = dimensionList.size();
    int dimension = ((Integer)dimensionList.get((i + size + n) % size)).intValue();
    setDimensionAndAdjustZoom(dimension);
  }
  
  public int getDimension() {
    return this.dimension;
  }
  
  public void setMapWH(int w, int h) {
    if (this.mapW != w || this.mapH != h) {
      this.mapW = w;
      this.mapH = h;
      updateBaseWH();
    } 
  }
  
  public void setMapWH(MapMode mapMode) {
    setMapWH(mapMode.wPixels, mapMode.hPixels);
  }
  
  public double getMinX() {
    return this.x - this.width / 2.0D;
  }
  
  public double getMaxX() {
    return this.x + this.width / 2.0D;
  }
  
  public double getMinZ() {
    return this.z - this.height / 2.0D;
  }
  
  public double getMaxZ() {
    return this.z + this.height / 2.0D;
  }
  
  public double getDimensionScaling(int playerDimension) {
    double scale;
    if (this.dimension != -1 && playerDimension == -1) {
      scale = 8.0D;
    } else if (this.dimension == -1 && playerDimension != -1) {
      scale = 0.125D;
    } else {
      scale = 1.0D;
    } 
    return scale;
  }
  
  public void setViewCentreScaled(double vX, double vZ, int playerDimension) {
    double scale = getDimensionScaling(playerDimension);
    setViewCentre(vX * scale, vZ * scale);
  }
  
  public void setTextureSize(int n) {
    if (this.textureSize != n) {
      this.textureSize = n;
      updateBaseWH();
    } 
  }
  
  private void updateBaseWH() {
    int w = this.mapW;
    int h = this.mapH;
    int halfTextureSize = this.textureSize / 2;
    while (w > halfTextureSize || h > halfTextureSize) {
      w /= 2;
      h /= 2;
    } 
    this.baseW = w;
    this.baseH = h;
    updateZoom();
  }
  
  public int getPixelsPerBlock() {
    return this.mapW / this.baseW;
  }
  
  public boolean isBlockWithinView(double bX, double bZ, boolean circular) {
    boolean inside;
    if (!circular) {
      inside = (bX > getMinX() || bX < getMaxX() || bZ > getMinZ() || bZ < getMaxZ());
    } else {
      double x = bX - this.x;
      double z = bZ - this.z;
      double r = getHeight() / 2.0D;
      inside = (x * x + z * z < r * r);
    } 
    return inside;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\MapView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */