package co.crystaldev.client.mixin.net.minecraft.world.pathfinder;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NodeProcessor.class})
public abstract class MixinNodeProcessor {
    @Shadow
    protected IBlockAccess blockaccess;

    @Inject(method = {"postProcess"}, at = {@At("HEAD")})
    private void cleanupBlockAccess(CallbackInfo ci) {
        this.blockaccess = null;
    }
}
