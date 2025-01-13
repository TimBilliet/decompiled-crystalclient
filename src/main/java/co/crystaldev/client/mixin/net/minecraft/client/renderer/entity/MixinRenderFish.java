package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.feature.impl.combat.OldAnimations;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RenderFish.class})
public abstract class MixinRenderFish {
    @Redirect(method = {"doRender"}, at = @At(value = "NEW", target = "net/minecraft/util/Vec3"))
    private Vec3 vec3(double x, double y, double z) {
        return ((OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertFishingRod) ? new Vec3(-0.5D, 0.03D, 0.8D) : new Vec3(-0.36D, 0.03D, 0.35D);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinRenderFish.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */