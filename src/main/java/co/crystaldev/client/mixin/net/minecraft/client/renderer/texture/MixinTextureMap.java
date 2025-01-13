package co.crystaldev.client.mixin.net.minecraft.client.renderer.texture;

import co.crystaldev.client.event.impl.init.TextureStitchEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TextureMap.class})
public abstract class MixinTextureMap {
    @Inject(method = {"loadTextureAtlas"}, at = {@At("HEAD")})
    public void loadTextureAtlasPre(IResourceManager resourceManager, CallbackInfo ci) {
        (new TextureStitchEvent.Pre(Minecraft.getMinecraft().getTextureMapBlocks())).call();
    }

    @Inject(method = {"loadTextureAtlas"}, at = {@At("TAIL")})
    public void loadTextureAtlasPost(IResourceManager resourceManager, CallbackInfo ci) {
        (new TextureStitchEvent.Post(Minecraft.getMinecraft().getTextureMapBlocks())).call();
    }
}
