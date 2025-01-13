package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiOptions.class})
public abstract class MixinGuiOptions extends GuiScreen {
    public void onGuiClosed() {
        this.mc.gameSettings.saveOptions();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */