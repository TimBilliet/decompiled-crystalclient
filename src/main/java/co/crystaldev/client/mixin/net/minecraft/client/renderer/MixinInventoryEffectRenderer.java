package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InventoryEffectRenderer.class})
public abstract class MixinInventoryEffectRenderer extends GuiContainer {
    @Shadow
    private boolean hasActivePotionEffects;

    public MixinInventoryEffectRenderer(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Inject(method = {"updateActivePotionEffects"}, cancellable = true, at = {@At("HEAD")})
    protected void updateActivePotionEffects(CallbackInfo ci) {
        ci.cancel();
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            this.guiLeft = !(ClientOptions.getInstance()).inventoryShiftFix ? (160 + (this.width - this.xSize - 200) / 2) : ((this.width - this.xSize) / 2);
            this.hasActivePotionEffects = true;
        } else {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }
}
