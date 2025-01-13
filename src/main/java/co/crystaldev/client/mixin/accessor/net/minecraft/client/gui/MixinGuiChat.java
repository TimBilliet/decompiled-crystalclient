package co.crystaldev.client.mixin.accessor.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GuiChat.class})
public interface MixinGuiChat {
    @Accessor("inputField")
    GuiTextField getInputField();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\gui\MixinGuiChat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */