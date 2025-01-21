package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.Client;
import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({OpenGlHelper.class})
public abstract class MixinOpenGlHelper {
    @Inject(method = {"setLightmapTextureCoords"}, at = {@At("RETURN")})
    private static void setLightmapTextureCoords(int target, float p_77475_1_, float p_77475_2_, CallbackInfo ci) {
        Client.setLastBrightnessX(p_77475_1_);
        Client.setLastBrightnessY(p_77475_2_);
    }
}
