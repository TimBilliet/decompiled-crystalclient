package co.crystaldev.client.mixin.net.minecraft.network.play.server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({S2EPacketCloseWindow.class})
public abstract class MixinS2EPacketCloseWindow {
    @Inject(method = {"processPacket(Lnet/minecraft/network/play/INetHandlerPlayClient;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void cancelGuiChatClose(INetHandlerPlayClient handler, CallbackInfo ci) {
        if ((Minecraft.getMinecraft()).currentScreen instanceof net.minecraft.client.gui.GuiChat)
            ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\network\play\server\MixinS2EPacketCloseWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */