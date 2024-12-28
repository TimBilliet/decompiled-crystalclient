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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\multiplayer\MixinGuiConnecting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */