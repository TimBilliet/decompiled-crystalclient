package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.handler.ClientCommandHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiChat.class})
public abstract class MixinGuiChat {
    @Inject(method = {"sendAutocompleteRequest"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", shift = At.Shift.BEFORE)})
    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_, CallbackInfo ci) {
        ClientCommandHandler.getInstance().autoComplete(p_146405_1_, p_146405_2_);
    }

    @Redirect(method = {"onAutocompleteResponse"}, at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;getCommonPrefix([Ljava/lang/String;)Ljava/lang/String;"))
    private String onAutocompleteResponse(String[] strs) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(StringUtils.getCommonPrefix(strs));
    }

    @ModifyArg(method = {"autocompletePlayerNames"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;writeText(Ljava/lang/String;)V"))
    private String autocompletePlayerNames(String in) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(in);
    }
}
