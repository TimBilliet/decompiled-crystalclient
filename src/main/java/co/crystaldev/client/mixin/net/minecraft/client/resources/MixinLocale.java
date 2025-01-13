package co.crystaldev.client.mixin.net.minecraft.client.resources;

import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({Locale.class})
public abstract class MixinLocale {
    @Inject(method = {"formatMessage"}, cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = {@At(value = "RETURN", ordinal = 1)})
    private void formatMessage(String translateKey, Object[] parameters, CallbackInfoReturnable<String> cir, String s) {
        cir.setReturnValue(s);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinLocale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */