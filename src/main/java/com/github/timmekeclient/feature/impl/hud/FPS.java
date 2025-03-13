package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.type.Tuple;
import net.minecraft.client.Minecraft;

@ConfigurableSize
@ModuleInfo(name = "FPS", description = "Displays your current FPS onscreen", category = Category.HUD)
public class FPS extends HudModuleBackground {
    public Tuple<String, String> getInfoHud() {
        return new Tuple("FPS", Integer.toString(Minecraft.getDebugFPS()));
    }

    public String getDisplayText() {
        return Minecraft.getDebugFPS() + " FPS";
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\FPS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */