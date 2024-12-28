package mchorse.mclib.utils.wav;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Waveform {
  public float[] average;
  
  public float[] maximum;
  
  private final List<WaveformSprite> sprites = new ArrayList<>();
  
  private int w;
  
  private int h;
  
  private int pixelsPerSecond;
  
  public void generate(Wave data, int pixelsPerSecond, int height) {
    if (data.getBytesPerSample() != 2)
      throw new IllegalStateException("Waveform generation doesn't support non 16-bit audio data!"); 
    populate(data, pixelsPerSecond, height);
    render();
  }
  
  public void render() {
    delete();
    int maxTextureSize = GL11.glGetInteger(3379) / 2;
    int count = (int)Math.ceil(this.w / maxTextureSize);
    int offset = 0;
    for (int t = 0; t < count; t++) {
      int texture = GlStateManager.generateTexture();
      int width = Math.min(this.w - offset, maxTextureSize);
      BufferedImage image = new BufferedImage(width, this.h, 2);
      Graphics g = image.getGraphics();
      for (int i = offset, j = 0, c = Math.min(offset + width, this.average.length); i < c; i++, j++) {
        float average = this.average[i];
        float maximum = this.maximum[i];
        int maxHeight = (int)(maximum * this.h);
        int avgHeight = (int)(average * (this.h - 1)) + 1;
        if (avgHeight > 0) {
          g.setColor(Color.WHITE);
          g.drawRect(j, this.h / 2 - maxHeight / 2, 1, maxHeight);
          g.setColor(Color.LIGHT_GRAY);
          g.drawRect(j, this.h / 2 - avgHeight / 2, 1, avgHeight);
        } 
      } 
      g.dispose();
      TextureUtil.uploadTextureImage(texture, image);
      this.sprites.add(new WaveformSprite(texture, width));
      offset += maxTextureSize;
    } 
  }
  
  public void populate(Wave data, int pixelsPerSecond, int height) {
    this.pixelsPerSecond = pixelsPerSecond;
    this.w = (int)(data.getDuration() * pixelsPerSecond);
    this.h = height;
    this.average = new float[this.w];
    this.maximum = new float[this.w];
    int region = data.getScanRegion(pixelsPerSecond);
    for (int i = 0; i < this.w; i++) {
      int offset = i * region;
      int count = 0;
      float average = 0.0F;
      float maximum = 0.0F;
      int j;
      for (j = 0; j < region && 
        offset + j + 1 < data.data.length; j += 2 * data.numChannels) {
        byte a = data.data[offset + j];
        byte b = data.data[offset + j + 1];
        float sample = (a + (b << 8));
        maximum = Math.max(maximum, Math.abs(sample));
        average += Math.abs(sample);
        count++;
      } 
      average /= count;
      average /= 32767.0F;
      maximum /= 32767.0F;
      this.average[i] = average;
      this.maximum[i] = maximum;
    } 
  }
  
  public void delete() {
    for (WaveformSprite sprite : this.sprites)
      GlStateManager.deleteTexture(sprite.texture); 
    this.sprites.clear();
  }
  
  public boolean isCreated() {
    return !this.sprites.isEmpty();
  }
  
  public int getPixelsPerSecond() {
    return this.pixelsPerSecond;
  }
  
  public int getWidth() {
    return this.w;
  }
  
  public int getHeight() {
    return this.h;
  }
  
  public void draw(int x, int y, int u, int v, int w, int h) {
    draw(x, y, u, v, w, h, this.h);
  }
  
  public void draw(int x, int y, int u, int v, int w, int h, int height) {
    int offset = 0;
    for (WaveformSprite sprite : this.sprites) {
      int sw = sprite.width;
      offset += sw;
      if (w <= 0)
        break; 
      if (u >= offset)
        continue; 
      int so = offset - u;
      GlStateManager.bindTexture(sprite.texture);
      x += so;
      u += so;
      w -= so;
    } 
  }
  
  public static class WaveformSprite {
    public final int texture;
    
    public final int width;
    
    public WaveformSprite(int texture, int width) {
      this.texture = texture;
      this.width = width;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\wav\Waveform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */