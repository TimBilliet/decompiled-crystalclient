package co.crystaldev.client.util.objects.resources.cosmetic;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.CosmeticEntry;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.gui.screens.override.ScreenMainMenu;
import co.crystaldev.client.util.objects.resources.FrameTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AnimatedCosmeticTexture extends AbstractTexture implements ITickable, ICosmeticTexture {
    private final ResourceLocation location;

    private final int texWidth;

    private final int texHeight;

    private FrameTexture[] images;

    private Integer[] frameIndexes;

    private int currentIndex = 0;

    private boolean loading = true, awaitingRegistering = false;

    public AnimatedCosmeticTexture(@NotNull CosmeticEntry entry) {
        this.location = entry.getResourceLocation();
        this.texWidth = entry.getOriginalWidth();
        this.texHeight = entry.getOriginalHeight();
    }

    public void loadTexture(IResourceManager resourceManager) {
    }

    public void tick() {
        if (this.loading || this.awaitingRegistering || this.frameIndexes == null)
            return;
        if (this.currentIndex++ >= this.frameIndexes.length - 1)
            this.currentIndex = 0;
    }

    public ITextureObject getCurrent() {
        if (this.loading) {
            CosmeticManager.getExecutor().submit(() -> {
                try {
                    SimpleResource resource = (SimpleResource) Minecraft.getMinecraft().getResourceManager().getResource(this.location);
                    AnimationMetadataSection animation = resource.getMetadata("animation");
                    this.images = new FrameTexture[animation.getFrameCount()];
                    this.frameIndexes = new Integer[animation.getFrameCount()];
                    int width = animation.getFrameWidth();
                    int height = animation.getFrameHeight();
                    BufferedImage image = TextureUtil.readBufferedImage(resource.getInputStream());
                    for (int i = 0; i < animation.getFrameCount(); i++) {
                        this.frameIndexes[i] = animation.getFrameIndex(i);
                        int index = animation.getFrameIndex(i);
                        if (index == i) {
                            int dWidth = (this.texWidth != -1) ? this.texWidth : width;
                            int dHeight = (this.texHeight != -1) ? this.texHeight : height;
                            BufferedImage frame = new BufferedImage(dWidth, dHeight, 1);
                            Graphics graphics = frame.getGraphics();
                            graphics.drawImage(image, 0, 0, width, height, 0, height * i, width, height * i + height, null);
                            graphics.dispose();
                            this.images[i] = new FrameTexture(frame);
                        } else {
                            this.images[i] = null;
                        }
                    }
                    this.loading = false;
                    this.awaitingRegistering = true;
                } catch (IOException ex) {
                    Reference.LOGGER.error("Unable to load animated cosmetic texture", ex);
                }
            });
            this.loading = false;
        }
        if (this.awaitingRegistering &&
                ScreenMainMenu.isAnimationComplete()) {
            for (FrameTexture frame : this.images) {
                if (frame != null)
                    frame.loadFrameTexture();
            }
            this.awaitingRegistering = false;
        }
        return (this.images == null || this.frameIndexes == null || this.frameIndexes[this.currentIndex] == null) ? null : this.images[this.frameIndexes[this.currentIndex]];
    }

    public boolean isTextureLoaded() {
        return (!this.loading && !this.awaitingRegistering);
    }

    public int getCurrentFrame() {
        return this.currentIndex;
    }

    public int getFrameCount() {
        return this.images.length;
    }
}
