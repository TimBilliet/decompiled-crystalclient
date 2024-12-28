package mapwriter.map.mapmode;

import co.crystaldev.client.feature.impl.hud.MapWriter;
import mapwriter.MapWriterMod;
import mapwriter.config.MapModeConfig;
import mapwriter.map.MapView;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.awt.geom.Point2D;

public class MapMode {
  private final Minecraft mc = Minecraft.getMinecraft();
  
  private final MapWriterMod mapwriter = MapWriterMod.getInstance();
  
  private final MapWriter mwMod = MapWriter.getInstance();
  
  public int xTranslation = 0;
  
  public int yTranslation = 0;
  
  public int x = -25;
  
  public int y = -25;
  
  public int w = 50;
  
  public int h = 50;
  
  public int wPixels = 50;
  
  public int hPixels = 50;
  
  public int textX = 0;
  
  public int textY = 0;
  
  public int textColor = -1;
  
  public MapModeConfig config;
  
  public MapMode(MapModeConfig config) {
    this.config = config;
  }
  
  public void update() {
    if (this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenMapWriter) {
      this.wPixels = this.mc.displayWidth;
      this.hPixels = this.mc.displayHeight;
      this.w = this.wPixels >> 1;
      this.h = this.hPixels >> 1;
      this.xTranslation = this.w >> 1;
      this.yTranslation = this.h >> 1;
      this.x = -this.xTranslation;
      this.y = -this.yTranslation;
      this.textX = this.textY = 0;
    } else if (this.mapwriter.miniMap != null && this.mapwriter.miniMap.currentMap == this.mapwriter.miniMap.largeMap) {
      int dw = this.mc.displayWidth / 2;
      int dh = this.mc.displayHeight / 2;
      this.w = this.h = (int)(Math.min(dw, dh) * 0.7F);
      this.wPixels = this.hPixels = this.w;
      this.x = -(this.w >> 1);
      this.y = -(this.h >> 1);
      this.xTranslation = dw / 2;
      this.yTranslation = dh / 2;
      this.textX = this.xTranslation;
      this.textY = this.yTranslation + (this.h >> 1) + 2 + this.mc.fontRendererObj.FONT_HEIGHT / 2;
    } else {
      this.w = this.h = (int)(this.mwMod.width * this.mwMod.scale);
      this.wPixels = this.hPixels = this.w;
      this.w &= 0xFFFFFFFE;
      this.h &= 0xFFFFFFFE;
      this.x = -(this.w >> 1);
      this.y = -(this.h >> 1);
      this.xTranslation = this.mwMod.getRenderX() + (this.w >> 1);
      this.yTranslation = this.mwMod.getRenderY() + (this.h >> 1);
      this.textX = this.xTranslation;
      this.textY = this.yTranslation + (this.h >> 1) + 2 + this.mc.fontRendererObj.FONT_HEIGHT / 2;
    } 
  }
  
  public Point screenXYtoBlockXZ(MapView mapView, int sx, int sy) {
    double withinMapX = ((float)sx - this.xTranslation) / this.w;//float
    double withinMapY = ((float)sy - this.yTranslation) / this.h;
    int bx = (int)Math.floor(mapView.getX() + withinMapX * mapView.getWidth());
    int bz = (int)Math.floor(mapView.getZ() + withinMapY * mapView.getHeight());
    return new Point(bx, bz);
  }
  
  public Point2D.Double blockXZtoScreenXY(MapView mapView, double bX, double bZ) {
    double xNorm = (bX - mapView.getX()) / mapView.getWidth();
    double zNorm = (bZ - mapView.getZ()) / mapView.getHeight();
    return new Point2D.Double(this.w * xNorm, this.h * zNorm);
  }
  
  public Point2D.Double getClampedScreenXY(MapView mapView, double bX, double bZ) {
    double xRel = (bX - mapView.getX()) / mapView.getWidth();
    double zRel = (bZ - mapView.getZ()) / mapView.getHeight();
    double limit = 0.49D;
    if (!this.config.circular) {
      if (xRel < -limit) {
        zRel = -limit * zRel / xRel;
        xRel = -limit;
      } 
      if (xRel > limit) {
        zRel = limit * zRel / xRel;
        xRel = limit;
      } 
      if (zRel < -limit) {
        xRel = -limit * xRel / zRel;
        zRel = -limit;
      } 
      if (zRel > limit) {
        xRel = limit * xRel / zRel;
        zRel = limit;
      } 
      if (xRel < -limit) {
        zRel = -limit * zRel / xRel;
        xRel = -limit;
      } 
      if (xRel > limit) {
        zRel = limit * zRel / xRel;
        xRel = limit;
      } 
    } else {
      double dSq = xRel * xRel + zRel * zRel;
      if (dSq > limit * limit) {
        double a = Math.atan2(zRel, xRel);
        xRel = limit * Math.cos(a);
        zRel = limit * Math.sin(a);
      } 
    } 
    return new Point2D.Double(this.w * xRel, this.h * zRel);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\mapmode\MapMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */