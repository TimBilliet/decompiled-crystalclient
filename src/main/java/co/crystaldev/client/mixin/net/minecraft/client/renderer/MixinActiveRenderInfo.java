package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.feature.impl.mechanic.PerspectiveMod;
import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ActiveRenderInfo.class})
public abstract class MixinActiveRenderInfo {
    @ModifyVariable(method = {"updateRenderInfo"}, at = @At("STORE"), ordinal = 2)
    private static float updateRenderInfo$f2(float f2) {
        return (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraPitch : f2;
    }

    @ModifyVariable(method = {"updateRenderInfo"}, at = @At("STORE"), ordinal = 3)
    private static float updateRenderInfo$f3(float f3) {
        return (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraYaw : f3;
    }
}