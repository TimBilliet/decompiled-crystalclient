package mapwriter.config;

import co.crystaldev.client.feature.impl.hud.MapWriter;

public class MapModeConfig {
    public boolean rotate = false;

    public boolean circular = false;

    public boolean borderMode = false;

    public int playerArrowSize = 5;

    public int markerSize = 5;

    public int trailMarkerSize = 3;

    public int alphaPercent = 100;

    public void loadConfig() {
        this.playerArrowSize = (MapWriter.getInstance()).playerArrowSize;
        this.alphaPercent = (MapWriter.getInstance()).backgroundAlpha;
        this.trailMarkerSize = Math.max(1, this.markerSize - 1);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\config\MapModeConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */