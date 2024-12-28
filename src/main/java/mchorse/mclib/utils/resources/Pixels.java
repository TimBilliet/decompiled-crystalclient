package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.Color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class Pixels {
  public byte[] pixelBytes;
  
  public int[] pixelInts;
  
  public int pixelLength;
  
  public int width;
  
  public int height;
  
  public Color color = new Color();
  
  private PixelAccessor accessor;
  
  public void set(BufferedImage image) {
    if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
      this.pixelBytes = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
      this.pixelInts = null;
      this.accessor = PixelAccessor.BYTE;
    } else {
      this.pixelInts = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
      this.pixelBytes = null;
      this.accessor = PixelAccessor.INT;
    } 
    this.pixelLength = (image.getAlphaRaster() != null) ? 4 : 3;
    this.width = image.getWidth();
    this.height = image.getHeight();
  }
  
  public boolean hasAlpha() {
    return (this.pixelLength == 4);
  }
  
  public int toIndex(int x, int y) {
    return x + y * this.width;
  }
  
  public int toX(int index) {
    return index % this.width;
  }
  
  public int toY(int index) {
    return index / this.width;
  }
  
  public int getCount() {
    return (this.accessor == PixelAccessor.BYTE) ? (this.pixelBytes.length / this.pixelLength) : this.pixelInts.length;
  }
  
  public Color getColor(int index) {
    this.accessor.get(this, index, this.color);
    return this.color;
  }
  
  public Color getColor(int x, int y) {
    return getColor(toIndex(x, y));
  }
  
  public void setColor(int index, Color color) {
    this.accessor.set(this, index, color);
  }
  
  public void setColor(int x, int y, Color color) {
    setColor(toIndex(x, y), color);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\Pixels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */