package mapwriter;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.feature.impl.hud.MapWriter;
import net.minecraft.client.settings.KeyBinding;

public class MwKeyHandler {
    private static final String category = "Timmeke_ Client - " + (MapWriter.getInstance()).name;

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
