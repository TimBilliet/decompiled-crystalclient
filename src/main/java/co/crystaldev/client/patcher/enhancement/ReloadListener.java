package co.crystaldev.client.patcher.enhancement;

import co.crystaldev.client.patcher.enhancement.text.EnhancedFontRenderer;
import co.crystaldev.client.patcher.hook.FontRendererHook;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances())
            enhancedFontRenderer.invalidateAll();
        FontRendererHook.forceRefresh = true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\ReloadListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */