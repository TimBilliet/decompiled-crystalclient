package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import co.crystaldev.client.gui.screens.override.multiplayer.ServerListExt;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerList.class})
public abstract class MixinServerList {
    @Inject(method = {"func_147414_b"}, at = {@At("HEAD")})
    private static void func_147414_b(ServerData serverdata, CallbackInfo ci) {
        ServerListExt.func_147414_b(serverdata);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\multiplayer\MixinServerList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */