package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.type.Tuple;

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
