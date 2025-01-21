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
