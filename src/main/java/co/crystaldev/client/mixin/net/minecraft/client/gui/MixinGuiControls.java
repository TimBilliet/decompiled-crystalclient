package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GuiControls.class})
public abstract class MixinGuiControls extends GuiScreen {
  @Shadow
  private GameSettings options;
  
  public void onGuiClosed() {
    super.onGuiClosed();
    this.options.saveOptions();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */