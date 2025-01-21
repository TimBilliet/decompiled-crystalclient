package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import co.crystaldev.client.Client;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiConnecting.class})
public abstract class MixinGuiConnecting {
    @Inject(method = {"connect"}, at = {@At("HEAD")})
    private void connect(String ip, int port, CallbackInfo ci) {
        Client.setCurrentServerIp(ip.toLowerCase().replaceAll("\\W+$", ""));
    }
}
