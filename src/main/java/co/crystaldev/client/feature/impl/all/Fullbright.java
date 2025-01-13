package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;

@ModuleInfo(name = "Fullbright", description = "Maxes your gamma", category = Category.ALL)
public class Fullbright extends Module {
    private static Fullbright INSTANCE;

    public Fullbright() {
        INSTANCE = this;
        this.enabled = false;
    }

    public void enable() {
        super.enable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public void disable() {
        super.disable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public static Fullbright getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\Fullbright.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */