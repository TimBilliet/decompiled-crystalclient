package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

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

    public boolean getDefaultForceDisabledState() {
        return Client.isOnHypixel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\OldHits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */