package co.crystaldev.client.cosmetic.type.cloak;

import co.crystaldev.client.cosmetic.base.CosmeticModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCloak extends CosmeticModel {
    private final ModelRenderer cloak;

    public ModelCloak() {
        this.cloak = new ModelRenderer(this, "cloak");
        this.cloak.setTextureSize(64, 32);
        this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, 0.0F);
    }

    public void render(float scale) {
        this.cloak.render(scale);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\type\cloak\ModelCloak.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */