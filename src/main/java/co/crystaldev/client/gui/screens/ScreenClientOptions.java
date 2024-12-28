package co.crystaldev.client.gui.screens;

import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.client.gui.GuiScreen;

public class ScreenClientOptions extends ScreenSettings {
  public ScreenClientOptions(GuiScreen parent) {
    super(ClientOptions.getInstance(), parent);
  }
}
