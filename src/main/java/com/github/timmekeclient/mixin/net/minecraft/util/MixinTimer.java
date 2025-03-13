package com.github.timmekeclient.mixin.net.minecraft.util;

import com.github.timmekeclient.Client;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Timer.class})
public abstract class MixinTimer {

    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    private void constructorTail(float tps, CallbackInfo ci) {
        Client.setTimer((Timer) (Object) this);
    }
}
