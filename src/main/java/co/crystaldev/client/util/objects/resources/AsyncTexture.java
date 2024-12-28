package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.Reference;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncTexture extends SimpleTexture {
  private BufferedImage bufferedImage;
  
  private boolean textureUploaded;
  
  public boolean loading = false;
  
  public boolean loadingComplete = false;
  
  private final int width;
  
  private final int height;
  
  protected static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  
  public AsyncTexture(ResourceLocation locationIn) {
    this(locationIn, -1, -1);
  }
  
  public AsyncTexture(ResourceLocation locationIn, int width, int height) {
    super(locationIn);
    this.width = width;
    this.height = height;
  }
  
  public void checkTextureUploaded() {
    if (!this.textureUploaded && 
      this.bufferedImage != null) {
      if (this.textureLocation != null)
        deleteGlTexture(); 
      TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
      this.textureUploaded = true;
    } 
  }
  
  public int getGlTextureId() {
    checkTextureUploaded();
    return super.getGlTextureId();
  }
  
  public void loadTexture(IResourceManager resourceManager) throws IOException {
    if (!this.loading && !this.loadingComplete) {
      this.loading = true;
      executor.submit(() -> {
            try {
              BufferedImage image = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
              if (this.width != -1 && this.height != -1 && image.getWidth() > this.width && image.getHeight() > this.height) {
                BufferedImage scaled = new BufferedImage(this.width, this.height, 1);
                Graphics graphics = scaled.getGraphics();
                graphics.drawImage(image, 0, 0, this.width, this.height, null);
                graphics.dispose();
                image = scaled;
              } 
              this.bufferedImage = image;
              this.loadingComplete = true;
              this.loading = false;
              checkTextureUploaded();
            } catch (IOException|NullPointerException ex) {
              Reference.LOGGER.error("Exception raised while reading resource", ex);
            } 
          });
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\AsyncTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */