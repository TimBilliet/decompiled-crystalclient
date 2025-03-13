package com.github.timmekeclient.mixin.net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VisGraph.class})
public abstract class MixinVisGraph {
    @Shadow
    private int field_178611_f;

    @Inject(method = {"computeVisibility"}, cancellable = true, at = {@At("HEAD")})
    public void computeVisibility(CallbackInfoReturnable<SetVisibility> ci) {
        if (4097 - this.field_178611_f < 256) {
            SetVisibility setVisibility = new SetVisibility();
            setVisibility.setAllVisible(true);
            ci.setReturnValue(setVisibility);
        }
    }
}
