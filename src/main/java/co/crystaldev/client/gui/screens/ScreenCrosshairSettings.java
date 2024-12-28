package co.crystaldev.client.gui.screens;

import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.impl.combat.CrosshairSettings;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.CrosshairButton;
import net.minecraft.client.gui.GuiScreen;

public class ScreenCrosshairSettings extends ScreenSettings {
  public ScreenCrosshairSettings(GuiScreen parent) {
    super((Module)CrosshairSettings.getInstance(), parent);
  }

  public void initSettings() {
    removeButton(b -> b.hasAttribute("config_option"));
    this.x = this.content.x + 14;
    this.x1 = this.content.x + this.content.width / 2 + 7;
    this.y = this.content.y + 5;
    this.w = this.content.width - 28;
    this.w1 = this.w / 2 - 7;
    this.h = 18;
    Pane scissor = this.content.scale(getScaledScreen());
    CrosshairButton button = new CrosshairButton(this.x + 20, this.y, this.w - 40, this.w / 5);
    button.addAttribute("config_option");
    button.setScissorPane(scissor);
    addButton((Button)button);
    this.y += button.height + 4;
    addSettingsButtons();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenCrosshairSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */