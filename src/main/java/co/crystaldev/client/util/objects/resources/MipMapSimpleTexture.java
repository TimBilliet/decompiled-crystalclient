package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.Reference;
import co.crystaldev.client.util.ClientTextureManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MipMapSimpleTexture extends AbstractTexture {
  private final ResourceLocation textureLocation;
  
  private final InputStream inputStream;
  
  public MipMapSimpleTexture(ResourceLocation textureLocationIn) {
    this.textureLocation = textureLocationIn;
    this.inputStream = null;
  }
  
  public MipMapSimpleTexture(InputStream inputStream) {
    this.textureLocation = null;
    this.inputStream = inputStream;
  }
  
  public void loadTexture(IResourceManager resourceManager) throws IOException {
    deleteGlTexture();
    InputStream is = null;
    try {
      IResource resource = (this.textureLocation == null) ? null : resourceManager.getResource(this.textureLocation);
      is = (resource == null) ? this.inputStream : resource.getInputStream();
      BufferedImage image = ImageIO.read(is);
      boolean flag = false;
      boolean flag1 = false;
      if (resource != null && resource.hasMetadata())
        try {
          TextureMetadataSection tms = (TextureMetadataSection)resource.getMetadata("texture");
          if (tms != null) {
            flag = tms.getTextureBlur();
            flag1 = tms.getTextureClamp();
          } 
        } catch (RuntimeException ex) {
          Reference.LOGGER.warn("Failed reading metadata of: " + this.textureLocation, ex);
        }  
      ClientTextureManager.uploadTextureImageAllocateMipMap(getGlTextureId(), image, flag, flag1);
    } finally {
      if (is != null)
        is.close(); 
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\MipMapSimpleTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */