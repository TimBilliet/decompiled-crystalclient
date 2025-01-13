package mapwriter;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.impl.hud.MapWriter;
import net.minecraft.client.settings.KeyBinding;

public class MwKeyHandler {
    private static final String category = "Crystal Client - " + (MapWriter.getInstance()).name;

    public static KeyBinding keyMapGui = new KeyBinding("key.mw_open_gui", 50, category);

    public static KeyBinding keyZoomIn = new KeyBinding("key.mw_zoom_in", 201, category);

    public static KeyBinding keyZoomOut = new KeyBinding("key.mw_zoom_out", 209, category);

    public static KeyBinding keyMapMode = new KeyBinding("key.mw_next_map_mode", 49, category);

    public static void registerKeyBindings() {
        Client.registerKeyBinding(keyMapGui);
        Client.registerKeyBinding(keyZoomIn);
        Client.registerKeyBinding(keyZoomOut);
        Client.registerKeyBinding(keyMapMode);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\MwKeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */