package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;

@ConfigurableSize
@ModuleInfo(name = "Memory", description = "Displays your memory usage onscreen", category = Category.HUD)
public class Memory extends HudModuleBackground {
    public Tuple<String, String> getInfoHud() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        return new Tuple("Memory", String.format("%2d%%", new Object[]{Long.valueOf(l * 100L / i)}));
    }

    public String getDisplayText() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        return String.format("Mem: %2d%%", new Object[]{Long.valueOf(l * 100L / i)});
    }
}
