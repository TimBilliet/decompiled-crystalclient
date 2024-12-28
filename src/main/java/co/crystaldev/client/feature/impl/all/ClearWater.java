package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

@ModuleInfo(name = "Clear Water", description = "Various features to improve water visibility", category = Category.ALL)
public class ClearWater extends Module {
    @Toggle(label = "Disable Water Fog")
    public boolean disableWaterFog = true;

    @Toggle(label = "Disable Lava Fog")
    public boolean disableLavaFog = true;

    @Toggle(label = "Disable Water FOV")
    public boolean disableWaterFOV = false;
    @Slider(label = "Overlay Transparency", placeholder = "{value}%", minimum = 0.0D, maximum = 100.0D, standard = 100.0D, integers = true)
    public int overlayTransparency = 5;

    private static ClearWater INSTANCE;


    public ClearWater() {
        INSTANCE = this;
    }

    public static ClearWater getInstance() {
        return INSTANCE;
    }
}