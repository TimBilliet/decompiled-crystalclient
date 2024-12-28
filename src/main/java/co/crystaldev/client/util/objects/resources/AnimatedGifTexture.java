package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.Reference;
import com.madgag.gif.fmsware.GifDecoder;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AnimatedGifTexture extends AbstractTexture implements ITickable {
  private final InputStream inputStream;

  private final ResourceLocation resourceLocation;

  private FrameTexture[] textures = null;

  private int index = 0;

  private long lastUpdate = 0L;

  private final int width;

  private final int height;

  public AnimatedGifTexture(ResourceLocation resourceLocation, int width, int height) {
    this.inputStream = null;
    this.resourceLocation = resourceLocation;
    this.width = width;
    this.height = height;
  }

  public AnimatedGifTexture(InputStream inputStream, int width, int height) {
    this.resourceLocation = null;
    this.width = width;
    this.height = height;
    this.inputStream = inputStream;
  }

  public void loadTexture(IResourceManager resourceManager) throws IOException {
    IResource resource = (this.resourceLocation == null) ? null : resourceManager.getResource(this.resourceLocation);
    GifDecoder gif = new GifDecoder();
    try (InputStream inputstream = (resource == null) ? this.inputStream : resource.getInputStream()) {
      int code = gif.read(inputstream);
      if (code != 0)
        Reference.LOGGER.debug("Error reading GIF, returned code " + code);
    }
    int n = gif.getFrameCount();
    if (this.textures == null) {
      this.textures = new FrameTexture[n];
    } else if (this.textures.length < n) {
      this.textures = Arrays.<FrameTexture>copyOf(this.textures, n);
    }
    for (int i = 0; i < n; i++) {
      FrameTexture texture = this.textures[i];
      if (texture == null)
        texture = new FrameTexture();
      texture.loadFrameTexture(gif.getFrame(i), this.width, this.height);
      texture.setDelay(gif.getDelay(i));
      this.textures[i] = texture;
    }
  }

  public void tick() {
    if (this.lastUpdate == 0L) {
      this.lastUpdate = System.currentTimeMillis();
      return;
    }
    FrameTexture current = this.textures[this.index];
    if (this.lastUpdate + current.getDelay() <= System.currentTimeMillis()) {
      this.lastUpdate = System.currentTimeMillis();
      increment();
    }
  }

  public void increment() {
    if (this.index++ >= this.textures.length - 1)
      this.index = 0;
  }

  public ITextureObject getCurrent() {
    return (ITextureObject)this.textures[this.index];
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\AnimatedGifTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */