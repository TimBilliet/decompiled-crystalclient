package co.crystaldev.client.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class FontTextureManager {
  public static List<Integer> textureIds = new ArrayList<>();
  
  public static int genTexture() {
    int textureId = GL11.glGenTextures();
    textureIds.add(textureId);
    return textureId;
  }
  
  public static void applyTexture(int texId, BufferedImage image, int filter, int wrap) {
    int[] pixels = new int[image.getWidth() * image.getHeight()];
    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int pixel = pixels[y * image.getWidth() + x];
        buffer.put((byte)(pixel >> 16 & 0xFF));
        buffer.put((byte)(pixel >> 8 & 0xFF));
        buffer.put((byte)(pixel & 0xFF));
        buffer.put((byte)(pixel >> 24 & 0xFF));
      } 
    } 
    buffer.flip();
    applyTexture(texId, image.getWidth(), image.getHeight(), buffer, filter, wrap);
  }
  
  public static void applyTexture(int texId, int width, int height, ByteBuffer pixels, int filter, int wrap) {
    GL11.glBindTexture(3553, texId);
    GL11.glTexParameteri(3553, 10241, filter);
    GL11.glTexParameteri(3553, 10240, filter);
    GL11.glTexParameteri(3553, 10242, wrap);
    GL11.glTexParameteri(3553, 10243, wrap);
    GL11.glPixelStorei(3317, 1);
    GL11.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, pixels);
    GL11.glBindTexture(3553, 0);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\font\FontTextureManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */