package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

@ModuleInfo(name = "Nametag Editor", description = "Edit entity nameplates", category = Category.ALL)
public class NametagEditor extends Module {
    @Toggle(label = "Show Crystal Client Logo")
    public boolean showCrystalClientLogo = true;

    @Toggle(label = "Show Orbit Client Logo")
    public boolean showOrbitClientLogo = true;

    @Toggle(label = "Text Shadow")
    public boolean textShadow = false;

    private static NametagEditor INSTANCE;

    public NametagEditor() {
        this.enabled = true;
        this.canBeDisabled = false;
        INSTANCE = this;
    }

    public static NametagEditor getInstance() {
        return INSTANCE;
    }
}
