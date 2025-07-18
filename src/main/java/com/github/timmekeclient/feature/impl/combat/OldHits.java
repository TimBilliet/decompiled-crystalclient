package com.github.timmekeclient.feature.impl.combat;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;

@ModuleInfo(name = "1.7 Hits", description = "Reverts the hit delay to how it was in 1.7", category = Category.COMBAT)
public class OldHits extends Module {
    private static OldHits INSTANCE;

    public OldHits() {
        INSTANCE = this;
        this.enabled = false;
    }

    public static OldHits getInstance() {
        return INSTANCE;
    }

}
