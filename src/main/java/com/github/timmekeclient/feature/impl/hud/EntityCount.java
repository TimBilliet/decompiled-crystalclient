package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.duck.RenderGlobalExt;
import com.github.timmekeclient.feature.annotations.ConfigurableSize;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.type.Tuple;

@ConfigurableSize
@ModuleInfo(name = "Entity Count", description = "Displays the total rendered entity count onscreen", category = Category.HUD)
public class EntityCount extends HudModuleBackground {
    public Tuple<String, String> getInfoHud() {
        return new Tuple("Entities", ((RenderGlobalExt) this.mc.renderGlobal).getHudEntityCount());
    }

    public String getDisplayText() {
        return "E: " + ((RenderGlobalExt) this.mc.renderGlobal).getHudEntityCount();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\EntityCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */