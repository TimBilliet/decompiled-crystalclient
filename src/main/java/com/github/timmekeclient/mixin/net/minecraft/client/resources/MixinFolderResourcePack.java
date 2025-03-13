package com.github.timmekeclient.mixin.net.minecraft.client.resources;

import com.github.timmekeclient.cosmetic.CosmeticResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin({FolderResourcePack.class})
public abstract class MixinFolderResourcePack {
    @Inject(method = {"getInputStreamByName"}, cancellable = true, at = {@At("HEAD")})
    private void getInputStreamByName(String name, CallbackInfoReturnable<InputStream> ci) {
        if (name.contains("timmekeclient") && !((Object) this instanceof CosmeticResourcePack))
            ci.setReturnValue(null);
    }

    @Inject(method = {"hasResourceName"}, cancellable = true, at = {@At("HEAD")})
    private void hasResourceName(String name, CallbackInfoReturnable<Boolean> ci) {
        if (name.contains("timmekeclient") && !((Object) this instanceof CosmeticResourcePack))
            ci.setReturnValue(Boolean.FALSE);
    }
}
