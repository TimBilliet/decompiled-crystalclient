package com.github.timmekeclient.mixin.net.minecraft.client.resources;

import net.minecraft.client.resources.FileResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin({FileResourcePack.class})
public abstract class MixinFileResourcePack {
    @Inject(method = {"getInputStreamByName"}, cancellable = true, at = {@At("HEAD")})
    private void getInputStreamByName(String name, CallbackInfoReturnable<InputStream> ci) {
        if (name.contains("timmekeclient"))
            ci.setReturnValue(null);
    }

    @Inject(method = {"hasResourceName"}, cancellable = true, at = {@At("HEAD")})
    private void hasResourceName(String name, CallbackInfoReturnable<Boolean> ci) {
        if (name.contains("timmekeclient"))
            ci.setReturnValue(Boolean.FALSE);
    }
}
