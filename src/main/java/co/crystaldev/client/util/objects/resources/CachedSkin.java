package co.crystaldev.client.util.objects.resources;

import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.task.DownloadImageTask;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class CachedSkin {
    private final UUID uuid;

    private final String url;

    private ITextureObject texture;

    public UUID getUuid() {
        return this.uuid;
    }

    private ResourceLocation resourceLocation = null;

    public CachedSkin(UUID uuid) {
        this.uuid = uuid;
        this.url = "https://mc-heads.net/avatar/" + this.uuid.toString();
        updateTexture();
    }

    public void updateTexture() {
        if (this.url == null) {
            this.texture = null;
        } else {
            this.texture = (ITextureObject) new DownloadImageTask(this.url, new HDImageBuffer());
            loadTexture();
        }
    }

    public void loadTexture() {
        if (this.resourceLocation == null) {
            this.resourceLocation = new ResourceLocation("crystalclient", "cache_" + this.uuid.toString());
            ClientTextureManager.getInstance().loadTextureMipMap(this.resourceLocation, this.texture);
        } else {
            ClientTextureManager.getInstance().getTexture(this.resourceLocation);
        }
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }
}