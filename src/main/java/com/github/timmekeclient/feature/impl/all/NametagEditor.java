package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;

@ModuleInfo(name = "Nametag Editor", description = "Edit entity nameplates", category = Category.ALL)
public class NametagEditor extends Module {

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
