package co.crystaldev.client.util.objects.dataprovider;

import mapwriter.api.IMwChunkOverlay;

import java.awt.*;

public class HoveredChunkHighlight implements IMwChunkOverlay {
  private final Point coordinates;

  private final int color;

  public HoveredChunkHighlight(int x, int z, int color) {
    this.coordinates = new Point(x, z);
    this.color = color;
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

  public void setCoordinates(int x, int z) {
    this.coordinates.x = x;
    this.coordinates.y = z;
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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\dataprovider\HoveredChunkHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */