package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;
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