package co.crystaldev.client.util.objects.dataprovider;

import co.crystaldev.client.util.RenderUtils;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;
import mapwriter.util.Render;

import java.awt.*;
import java.awt.geom.Point2D;

public class GroupChunkHighlight implements IMwChunkOverlay {
  private final Point coordinates;

  private final int color;

  private final String text;

  public String getText() {
    return this.text;
  }

  public GroupChunkHighlight(int x, int z, int color, String text) {
    this.coordinates = new Point(x, z);
    this.color = color;
    this.text = text;
  }

  public Point getCoordinates() {
    return this.coordinates;
  }

  public int getColor() {
    return this.color;
  }

  public float getFilling() {
    return 1.0F;
  }

  public void draw(MapMode mapMode, MapView mapView) {
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
    Render.setColour(getColor());
    Render.drawRect(topCorner.x + offsetX, topCorner.y + offsetY, sizeX, sizeY);
    double size = (sizeX - 1.0D) / 2.0D;
    RenderUtils.drawCenteredString(this.text, (int)(topCorner.x + offsetX + size), (int)(topCorner.y + offsetY + size), 16777215);
  }

  public boolean hasBorder() {
    return false;
  }

  public float getBorderWidth() {
    return 0.0F;
  }

  public int getBorderColor() {
    return 0;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\dataprovider\GroupChunkHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */