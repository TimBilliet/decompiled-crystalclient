package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiLanguage.class})
public abstract class MixinGuiLanguage extends GuiScreen {
    public void onGuiClosed() {
        this.mc.ingameGUI.getChatGUI().refreshChat();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiLanguage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */