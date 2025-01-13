package co.crystaldev.client.mixin.net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({C17PacketCustomPayload.class})
public abstract class MixinC17PacketCustomPayload {
    @Shadow
    private PacketBuffer data;

    @Inject(method = {"processPacket(Lnet/minecraft/network/play/INetHandlerPlayServer;)V"}, at = {@At("TAIL")})
    private void releaseData(INetHandlerPlayServer handler, CallbackInfo ci) {
        if (this.data != null)
            this.data.release();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\network\play\client\MixinC17PacketCustomPayload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */