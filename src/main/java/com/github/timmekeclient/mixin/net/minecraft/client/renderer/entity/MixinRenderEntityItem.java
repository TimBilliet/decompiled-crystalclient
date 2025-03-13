package com.github.timmekeclient.mixin.net.minecraft.client.renderer.entity;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderEntityItem.class})
public abstract class MixinRenderEntityItem {
    @Inject(method = {"func_177078_a"}, at = {@At("HEAD")}, cancellable = true)
    private void disableStackedItems(CallbackInfoReturnable<Integer> cir) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableStackedItems))
            cir.setReturnValue(1);
    }
}