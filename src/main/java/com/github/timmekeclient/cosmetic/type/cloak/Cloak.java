package com.github.timmekeclient.cosmetic.type.cloak;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.cosmetic.CosmeticEntry;
import com.github.timmekeclient.cosmetic.base.Cosmetic;
import com.github.timmekeclient.util.ClientTextureManager;
import com.github.timmekeclient.util.objects.resources.cosmetic.CosmeticTexture;
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