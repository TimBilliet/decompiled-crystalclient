package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TileEntityBannerRenderer.class})
public abstract class MixinTileEntityBannerRenderer {
    @Redirect(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityBanner;DDDFI)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTotalWorldTime()J"))
    private long resolveOverflow(World world) {
        return world.getTotalWorldTime() % 100L;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntityBannerRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */