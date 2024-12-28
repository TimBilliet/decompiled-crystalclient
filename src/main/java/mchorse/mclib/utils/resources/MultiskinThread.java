package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Stack;

public class MultiskinThread implements Runnable {
  private static MultiskinThread instance;
  
  private static Thread thread;
  
  public Stack<MultiResourceLocation> locations = new Stack<>();
  
  public static synchronized void add(MultiResourceLocation location) {
    if (instance != null && !thread.isAlive())
      instance = null; 
    if (instance == null) {
      instance = new MultiskinThread();
      instance.addLocation(location);
      thread = new Thread(instance);
      thread.start();
    } else {
      instance.addLocation(location);
    } 
  }
  
  public static void clear() {
    instance = null;
  }
  
  public static ByteBuffer bytesFromBuffer(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    ByteBuffer buffer = GLAllocation.createDirectByteBuffer(w * h * 4);
    int[] pixels = new int[w * h];
    image.getRGB(0, 0, w, h, pixels, 0, w);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int pixel = pixels[y * w + x];
        buffer.put((byte)(pixel >> 16 & 0xFF));
        buffer.put((byte)(pixel >> 8 & 0xFF));
        buffer.put((byte)(pixel & 0xFF));
        buffer.put((byte)(pixel >> 24 & 0xFF));
      } 
    } 
    buffer.flip();
    return buffer;
  }
  
  public synchronized void addLocation(MultiResourceLocation location) {
    if (this.locations.contains(location))
      return; 
    this.locations.add(location);
  }
  
  public void run() {
    while (!this.locations.isEmpty() && instance != null) {
      MultiResourceLocation location = this.locations.peek();
      ITextureObject texture = (ITextureObject)ReflectionUtils.getTextures(Minecraft.getMinecraft().getTextureManager()).get(location);
      try {
        if (texture != null) {
          this.locations.pop();
          BufferedImage image = TextureProcessor.postProcess(location);
          int w = image.getWidth();
          int h = image.getHeight();
          ByteBuffer buffer = bytesFromBuffer(image);
          Minecraft.getMinecraft().addScheduledTask(() -> {
                TextureUtil.allocateTexture(texture.getGlTextureId(), w, h);
                GL11.glBindTexture(3553, texture.getGlTextureId());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 32856, w, h, 0, 6408, 5121, buffer);
              });
        } 
        Thread.sleep(100L);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
    instance = null;
    thread = null;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\MultiskinThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */