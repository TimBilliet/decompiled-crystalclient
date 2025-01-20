package co.crystaldev.client.util.objects.resources.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.CosmeticEntry;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class CosmeticTexture extends AbstractTexture {
    private final ResourceLocation location;

    private final int width;

    private final int height;

    public CosmeticTexture(@NotNull CosmeticEntry entry) {
        this.location = entry.getResourceLocation();
        this.width = entry.getOriginalWidth();
        this.height = entry.getOriginalHeight();
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        deleteGlTexture();
        InputStream stream = null;
        try {
            IResource resource = resourceManager.getResource(this.location);
            stream = resource.getInputStream();
            BufferedImage image = TextureUtil.readBufferedImage(stream);
            boolean blur = false, clamp = false;
            if (this.width != -1 || this.height != -1) {
                BufferedImage newImage = new BufferedImage((this.width == -1) ? image.getWidth() : this.width, (this.height == -1) ? image.getHeight() : this.height, 2);
                Graphics graphics = newImage.getGraphics();
                graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                graphics.dispose();
                image = newImage;
            }
            if (resource.hasMetadata())
                try {
                    TextureMetadataSection texturemetadatasection = (TextureMetadataSection) resource.getMetadata("texture");
                    if (texturemetadatasection != null) {
                        blur = texturemetadatasection.getTextureBlur();
                        clamp = texturemetadatasection.getTextureClamp();
                    }
                } catch (RuntimeException ex) {
                    Reference.LOGGER.warn("Failed reading metadata of {}", new Object[]{this.location, ex});
                }
            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, blur, clamp);
        } finally {
            if (stream != null)
                IOUtils.closeQuietly(stream);
        }
    }
}
