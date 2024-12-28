package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.duck.GameSettingsExt;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiVideoSettings.class})
public abstract class MixinGuiVideoSettings extends GuiScreen {
  public void onGuiClosed() {
    super.onGuiClosed();
    ((GameSettingsExt)this.mc.gameSettings).onSettingsGuiClosed();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiVideoSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */