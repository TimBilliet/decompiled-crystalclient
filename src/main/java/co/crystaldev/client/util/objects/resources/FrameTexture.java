package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.util.ClientTextureManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FrameTexture extends AbstractTexture {
  private int delay;
  
  private BufferedImage frame;
  
  public int getDelay() {
    return this.delay;
  }
  
  public void setDelay(int delay) {
    this.delay = delay;
  }
  
  public FrameTexture(BufferedImage frame) {
    this.frame = frame;
  }
  
  public FrameTexture() {}
  
  public void loadFrameTexture() {
    if (this.frame == null)
      return; 
    deleteGlTexture();
    ClientTextureManager.uploadTextureImageAllocateMipMap(getGlTextureId(), this.frame, false, false);
    this.frame = null;
  }
  
  public void loadFrameTexture(BufferedImage image) {
    deleteGlTexture();
    ClientTextureManager.uploadTextureImageAllocateMipMap(getGlTextureId(), image, false, false);
  }
  
  public void loadFrameTexture(BufferedImage image, int width, int height) {
    deleteGlTexture();
    if (width != -1 && height != -1) {
      BufferedImage scaled = new BufferedImage(width, height, 1);
      Graphics graphics = scaled.getGraphics();
      graphics.drawImage(image, 0, 0, width, height, null);
      graphics.dispose();
      image = scaled;
    } 
    ClientTextureManager.uploadTextureImageAllocateMipMap(getGlTextureId(), image, false, false);
  }
  
  public void loadTexture(IResourceManager resourceManager) throws IOException {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\FrameTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */