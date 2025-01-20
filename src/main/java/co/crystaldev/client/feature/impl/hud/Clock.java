package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Selector;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ConfigurableSize
@ModuleInfo(name = "Clock", description = "Displays the current time onscreen", category = Category.HUD)
public class Clock extends HudModuleBackground {
    @Selector(label = "Format", values = {"hh:mm a", "hh:mm:ss a", "HH:mm", "HH:mm:ss"})
    public String format = "hh:mm a";

    private final Map<String, DateTimeFormatter> formatters;

    public Clock() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.width = 60;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 70.0F, 73.0F);
        this.formatters = new HashMap<>();
        for (String value : new String[]{"hh:mm a", "hh:mm:ss a", "HH:mm", "HH:mm:ss"})
            this.formatters.put(value, DateTimeFormatter.ofPattern(value));
    }

    public String getDisplayText() {
        return getTime();
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("Clock", getTime());
    }

    private String getTime() {
        String time = ((DateTimeFormatter) this.formatters.get(this.format)).format(LocalDateTime.now());
        return time.startsWith("0") ? time.substring(1) : time;
    }
}
