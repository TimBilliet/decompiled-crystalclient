package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.util.ColorObject;

@ModuleInfo(name = "Hit Color", description = "Modify the vanilla Minecraft damage indicator color", category = Category.COMBAT)
public class HitColor extends Module {
    @Colour(label = "Color")
    public ColorObject color = new ColorObject(255, 0, 0, 75);

    private static HitColor INSTANCE;

    public HitColor() {
        INSTANCE = this;
        this.enabled = false;
    }

    public static HitColor getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\HitColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */