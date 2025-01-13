package co.crystaldev.client.mixin.net.minecraft.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PacketBuffer.class})
public abstract class MixinPacketBuffer {
    @Shadow
    public abstract String readStringFromBuffer(int paramInt);

    @Inject(method = {"readChatComponent"}, at = {@At("HEAD")}, cancellable = true)
    public void readChatComponent(CallbackInfoReturnable<IChatComponent> ci) {
        String str = readStringFromBuffer(32767);
        int exploitIndex = str.indexOf("${");
        if (exploitIndex != -1 && str.lastIndexOf("}") > exploitIndex)
            str = str.replaceAll("\\$\\{", "\\$\000{");
        ci.setReturnValue(IChatComponent.Serializer.jsonToComponent(str));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\network\MixinPacketBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */