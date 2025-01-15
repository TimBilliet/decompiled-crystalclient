package co.crystaldev.client.cosmetic.type.cloak;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.CosmeticEntry;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.objects.resources.cosmetic.CosmeticTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import org.jetbrains.annotations.Nullable;

public class Cloak extends Cosmetic {
    public Cloak(@Nullable CosmeticEntry entry) {
        super(entry);
        if (entry == null)
            return;
        try {
            this.texture = new CosmeticTexture(entry);
            ClientTextureManager.getInstance().loadTextureMipMap(getLocation(), this.texture);
        } catch (Throwable ex) {
            Reference.LOGGER.error("Unable to fetch resource for cosmetic '{}'", getLocation(), ex);
        }
    }

    public void bindTexture() {
        if (this.texture != null)
            ClientTextureManager.getInstance().bindTexture(this.texture);
    }
}