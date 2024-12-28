package mapwriter.api;

import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;
import mapwriter.util.Render;

import java.awt.*;
import java.awt.geom.Point2D;

public interface IMwChunkOverlay {
  Point getCoordinates();
  
  int getColor();
  
  float getFilling();
  
  boolean hasBorder();
  
  float getBorderWidth();
  
  int getBorderColor();
  
  default void draw(MapMode mapMode, MapView mapView) {
    int chunkX = (getCoordinates()).x;
    int chunkZ = (getCoordinates()).y;
    float filling = getFilling();
    Point2D.Double topCorner = mapMode.blockXZtoScreenXY(mapView, (chunkX << 4), (chunkZ << 4));
    Point2D.Double botCorner = mapMode.blockXZtoScreenXY(mapView, (chunkX + 1 << 4), (chunkZ + 1 << 4));
    topCorner.x = Math.max(mapMode.x, topCorner.x);
    topCorner.x = Math.min((mapMode.x + mapMode.w), topCorner.x);
    topCorner.y = Math.max(mapMode.y, topCorner.y);
    topCorner.y = Math.min((mapMode.y + mapMode.h), topCorner.y);
    botCorner.x = Math.max(mapMode.x, botCorner.x);
    botCorner.x = Math.min((mapMode.x + mapMode.w), botCorner.x);
    botCorner.y = Math.max(mapMode.y, botCorner.y);
    botCorner.y = Math.min((mapMode.y + mapMode.h), botCorner.y);
    double sizeX = (botCorner.x - topCorner.x) * filling;
    double sizeY = (botCorner.y - topCorner.y) * filling;
    double offsetX = (botCorner.x - topCorner.x - sizeX) / 2.0D;
    double offsetY = (botCorner.y - topCorner.y - sizeY) / 2.0D;
    if (hasBorder()) {
      Render.setColour(getBorderColor());
      Render.drawRectBorder(topCorner.x + 1.0D, topCorner.y + 1.0D, botCorner.x - topCorner.x - 1.0D, botCorner.y - topCorner.y - 1.0D, getBorderWidth());
    } 
    Render.setColour(getColor());
    Render.drawRect(topCorner.x + offsetX + 1.0D, topCorner.y + offsetY + 1.0D, sizeX - 1.0D, sizeY - 1.0D);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\api\IMwChunkOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */