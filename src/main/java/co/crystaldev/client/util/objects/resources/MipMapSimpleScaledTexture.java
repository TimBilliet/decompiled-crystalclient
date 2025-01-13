package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.Reference;
import co.crystaldev.client.util.ClientTextureManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MipMapSimpleScaledTexture extends AbstractTexture {
    private final ResourceLocation textureLocation;

    private final InputStream inputStream;

    private final int width;

    private final int height;

    public MipMapSimpleScaledTexture(ResourceLocation textureLocationIn, int width, int height) {
        this.textureLocation = textureLocationIn;
        this.inputStream = null;
        this.width = width;
        this.height = height;
    }

    public MipMapSimpleScaledTexture(InputStream inputStream, int width, int height) {
        this.textureLocation = null;
        this.inputStream = inputStream;
        this.width = width;
        this.height = height;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        deleteGlTexture();
        InputStream is = null;
        try {
            IResource resource = (this.textureLocation == null) ? null : resourceManager.getResource(this.textureLocation);
            is = (resource == null) ? this.inputStream : resource.getInputStream();
            BufferedImage image = ImageIO.read(is);
            if (image.getWidth() > this.width && image.getHeight() > this.height) {
                BufferedImage scaled = new BufferedImage(this.width, this.height, 1);
                Graphics graphics = scaled.getGraphics();
                graphics.drawImage(image, 0, 0, this.width, this.height, null);
                graphics.dispose();
                image = scaled;
            }
            boolean flag = false;
            boolean flag1 = false;
            if (resource != null && resource.hasMetadata())
                try {
                    TextureMetadataSection tms = (TextureMetadataSection) resource.getMetadata("texture");
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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\resources\MipMapSimpleScaledTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */