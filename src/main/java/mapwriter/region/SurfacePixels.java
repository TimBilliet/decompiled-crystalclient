package mapwriter.region;

import mapwriter.util.Logging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SurfacePixels {
  protected Region region;
  
  protected File filename;
  
  protected int[] pixels = null;
  
  protected boolean cannotLoad = false;
  
  protected int updateCount = 0;
  
  public SurfacePixels(Region region, File filename) {
    this.region = region;
    this.filename = filename;
  }
  
  public void clear() {
    if (this.pixels != null)
      Arrays.fill(this.pixels, 0); 
  }
  
  public void close() {
    if (this.updateCount > 0)
      save(); 
    this.pixels = null;
  }
  
  private void save() {
    if (this.pixels != null) {
      saveImage(this.filename, this.pixels, 512, 512);
      this.cannotLoad = false;
    } 
    this.updateCount = 0;
  }
  
  private void load() {
    if (!this.cannotLoad) {
      this.pixels = loadImage(this.filename, 512, 512);
      if (this.pixels != null) {
        for (int i = 0; i < this.pixels.length; i++) {
          int colour = this.pixels[i];
          if (colour == -16777216)
            this.pixels[i] = 0; 
        } 
      } else {
        this.cannotLoad = true;
      } 
      this.updateCount = 0;
    } 
  }
  
  public int[] getPixels() {
    if (this.pixels == null)
      load(); 
    return this.pixels;
  }
  
  public int[] getOrAllocatePixels() {
    getPixels();
    if (this.pixels == null) {
      this.pixels = new int[262144];
      clear();
    } 
    return this.pixels;
  }
  
  public void updateChunk(MwChunk chunk) {
    int x = chunk.x << 4;
    int z = chunk.z << 4;
    int offset = this.region.getPixelOffset(x, z);
    int[] pixels = getOrAllocatePixels();
    ChunkRender.renderSurface(this.region.regionManager.blockColours, chunk, pixels, offset, 512, (chunk.dimension == -1));
    this.region.updateZoomLevels(x, z, 16, 16);
    this.updateCount++;
  }
  
  public static int getAverageOfPixelQuad(int[] pixels, int offset, int scanSize) {
    int p00 = pixels[offset];
    int p01 = pixels[offset + 1];
    int p10 = pixels[offset + scanSize];
    int p11 = pixels[offset + scanSize + 1];
    int r = (p00 >> 16 & 0xFF) + (p01 >> 16 & 0xFF) + (p10 >> 16 & 0xFF) + (p11 >> 16 & 0xFF);
    r >>= 2;
    int g = (p00 >> 8 & 0xFF) + (p01 >> 8 & 0xFF) + (p10 >> 8 & 0xFF) + (p11 >> 8 & 0xFF);
    g >>= 2;
    int b = (p00 & 0xFF) + (p01 & 0xFF) + (p10 & 0xFF) + (p11 & 0xFF);
    b >>= 2;
    return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
  }
  
  public void updateScaled(int[] srcPixels, int srcX, int srcZ, int dstX, int dstZ, int dstW, int dstH) {
    int[] dstPixels = getOrAllocatePixels();
    for (int j = 0; j < dstH; j++) {
      for (int i = 0; i < dstW; i++) {
        int srcOffset = (srcZ + j * 2 << 9) + srcX + i * 2;
        int dstPixel = getAverageOfPixelQuad(srcPixels, srcOffset, 512);
        dstPixels[(dstZ + j << 9) + dstX + i] = dstPixel;
      } 
    } 
    this.updateCount++;
  }
  
  public static void saveImage(File filename, int[] pixels, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, 2);
    img.setRGB(0, 0, w, h, pixels, 0, w);
    try {
      ImageIO.write(img, "png", filename);
    } catch (IOException e) {
      Logging.logError("saveImage: error: could not write image to %s", new Object[] { filename });
    } 
  }
  
  public static int[] loadImage(File filename, int w, int h) {
    BufferedImage img = null;
    try {
      img = ImageIO.read(filename);
    } catch (IOException e) {
      img = null;
    } 
    int[] pixels = null;
    if (img != null)
      if (img.getWidth() == w && img.getHeight() == h) {
        pixels = new int[w * h];
        img.getRGB(0, 0, w, h, pixels, 0, w);
      } else {
        Logging.logWarning("loadImage: image '%s' does not match expected dimensions (got %dx%d expected %dx%d)", new Object[] { filename, Integer.valueOf(img.getWidth()), Integer.valueOf(img.getHeight()), Integer.valueOf(w), Integer.valueOf(h) });
      }  
    return pixels;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\SurfacePixels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */