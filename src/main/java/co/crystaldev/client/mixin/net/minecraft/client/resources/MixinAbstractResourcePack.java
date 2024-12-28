package co.crystaldev.client.mixin.net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.AbstractResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Mixin({AbstractResourcePack.class})
public abstract class MixinAbstractResourcePack {
  /**
   * @author Tim
   */
  @Overwrite
  public BufferedImage getPackImage() throws IOException {
    BufferedImage image = TextureUtil.readBufferedImage(getInputStreamByName("pack.png"));
    if (image == null)
      return null; 
    BufferedImage scaledImage = new BufferedImage(64, 64, 2);
    Graphics graphics = scaledImage.getGraphics();
    graphics.drawImage(image, 0, 0, 64, 64, null);
    graphics.dispose();
    return scaledImage;
  }
  
  @Shadow
  protected abstract InputStream getInputStreamByName(String paramString);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinAbstractResourcePack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */