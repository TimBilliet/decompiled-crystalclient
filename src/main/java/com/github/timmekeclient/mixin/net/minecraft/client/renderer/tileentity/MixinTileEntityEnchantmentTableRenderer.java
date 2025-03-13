package com.github.timmekeclient.mixin.net.minecraft.client.renderer.tileentity;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityEnchantmentTableRenderer.class})
public abstract class MixinTileEntityEnchantmentTableRenderer {
    @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityEnchantmentTable;DDDFI)V"}, at = {@At("HEAD")}, cancellable = true)
    private void cancelRendering(CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableEnchantmentTableBooks))
            ci.cancel();
    }
}
