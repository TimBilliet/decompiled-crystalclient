package co.crystaldev.client.util.objects.resources;

import net.minecraft.client.renderer.IImageBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HDImageBuffer implements IImageBuffer {
    public BufferedImage parseUserSkin(BufferedImage texture) {
        if (texture == null)
            return null;
        int w = Math.max(texture.getWidth(), 64);
        int h = Math.max(texture.getHeight(), 32);
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics gfx = image.getGraphics();
        gfx.drawImage(texture, 0, 0, null);
        gfx.dispose();
        return image;
    }

    public void skinAvailable() {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\HDImageBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */