package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TextureProcessor {
    public static Pixels pixels = new Pixels();

    public static Pixels target = new Pixels();

    public static BufferedImage postProcess(MultiResourceLocation multi) {
        return process(multi);
    }

    public static BufferedImage process(MultiResourceLocation multi) {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        List<BufferedImage> images = new ArrayList<>();
        int w = 0;
        int h = 0;
        for (int i = 0; i < multi.children.size(); i++) {
            FilteredResourceLocation child = multi.children.get(i);
            try {
                IResource resource = manager.getResource(child.path);
                BufferedImage bufferedImage = ImageIO.read(resource.getInputStream());
                images.add(bufferedImage);
                w = Math.max(w, bufferedImage.getWidth());
                h = Math.max(h, bufferedImage.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics g = image.getGraphics();
        for (int j = 0; j < multi.children.size(); j++) {
            BufferedImage child = images.get(j);
            FilteredResourceLocation filter = multi.children.get(j);
            int iw = child.getWidth();
            int ih = child.getHeight();
            if (filter.scaleToLargest) {
                iw = w;
                ih = h;
            } else if (filter.scale != 0.0F && filter.scale > 0.0F) {
                iw = (int) (iw * filter.scale);
                ih = (int) (ih * filter.scale);
            }
            if (iw > 0 && ih > 0)
                if (filter.erase) {
                    processErase(image, child, filter, iw, ih);
                } else {
                    if (filter.color != 16777215 || filter.pixelate > 1)
                        processImage(child, filter);
                    g.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
                }
        }
        g.dispose();
        return image;
    }

    private static void processErase(BufferedImage image, BufferedImage child, FilteredResourceLocation filter, int iw, int ih) {
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g2 = mask.getGraphics();
        g2.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
        g2.dispose();
        target.set(mask);
        pixels.set(image);
        for (int p = 0, c = target.getCount(); p < c; p++) {
            Color pixel = target.getColor(p);
            if (pixel.a > 0.999F) {
                pixel = pixels.getColor(p);
                pixel.a = 0.0F;
                pixels.setColor(p, pixel);
            }
        }
    }

    private static void processImage(BufferedImage child, FilteredResourceLocation frl) {
        pixels.set(child);
        Color filter = (new Color()).set(frl.color);
        Color pixel = new Color();
        for (int i = 0, c = pixels.getCount(); i < c; i++) {
            pixel.copy(pixels.getColor(i));
            if (pixels.hasAlpha() &&
                    pixel.a <= 0.0F)
                continue;
            if (frl.pixelate > 1) {
                int x = pixels.toX(i);
                int y = pixels.toY(i);
                boolean origin = (x % frl.pixelate == 0 && y % frl.pixelate == 0);
                x -= x % frl.pixelate;
                y -= y % frl.pixelate;
                pixel.copy(pixels.getColor(x, y));
                pixels.setColor(i, pixel);
                if (!origin)
                    continue;
            }
            pixel.r *= filter.r;
            pixel.g *= filter.g;
            pixel.b *= filter.b;
            pixel.a *= filter.a;
            pixels.setColor(i, pixel);
            continue;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\TextureProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */