package mapwriter.config;

import co.crystaldev.client.feature.impl.hud.MapWriter;

public class LargeMapConfig extends MapModeConfig {
  public void loadConfig() {
    super.loadConfig();
    this.rotate = (MapWriter.getInstance()).rotate;
    this.circular = (MapWriter.getInstance()).circular;
    this.borderMode = (MapWriter.getInstance()).mapBorder;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\config\LargeMapConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */