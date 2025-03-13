package com.github.timmekeclient.cosmetic.type.cloak;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.cosmetic.CosmeticEntry;
import com.github.timmekeclient.cosmetic.base.Cosmetic;
import com.github.timmekeclient.mixin.accessor.net.minecraft.client.renderer.texture.MixinTextureManager;
import com.github.timmekeclient.util.ClientTextureManager;
import com.github.timmekeclient.util.objects.resources.cosmetic.AnimatedCosmeticTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;

public class AnimatedCloak extends Cosmetic {
    public AnimatedCloak(@Nullable CosmeticEntry entry) {
        super(entry);
        if (entry == null)
            return;
        try {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            this.texture = new AnimatedCosmeticTexture(entry);
            textureManager.loadTexture(getLocation(), this.texture);
            ((MixinTextureManager) textureManager).getListTickables().add((ITickable) this.texture);
        } catch (Throwable ex) {
            Reference.LOGGER.error("Unable to fetch resource for cosmetic '{}'", getLocation(), ex);
        }
    }

    public void bindTexture() {
        ClientTextureManager.getInstance().bindTexture(((AnimatedCosmeticTexture) this.texture).getCurrent());
    }
}