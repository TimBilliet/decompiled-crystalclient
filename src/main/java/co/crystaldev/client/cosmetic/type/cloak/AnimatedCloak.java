package co.crystaldev.client.cosmetic.type.cloak;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cosmetic.CosmeticEntry;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.texture.MixinTextureManager;
//import co.crystaldev.client.util.ClientTextureManager;
//import co.crystaldev.client.util.objects.resources.cosmetic.AnimatedCosmeticTexture;
import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.objects.resources.cosmetic.AnimatedCosmeticTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\type\cloak\AnimatedCloak.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */