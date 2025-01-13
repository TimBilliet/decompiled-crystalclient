package co.crystaldev.client.mixin.optifine.net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.block.model.FaceBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({FaceBakery.class})
public abstract class MixinFaceBakery {
    @ModifyConstant(method = {"storeVertexData"}, constant = {@Constant(doubleValue = 0.999D)})
    private double tweak999Weight(double d) {
        return 1.0D;
    }

    @ModifyConstant(method = {"storeVertexData"}, constant = {@Constant(doubleValue = 0.001D)})
    private double tweak001Weight(double d) {
        return 0.0D;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\optifine\net\minecraft\client\renderer\block\model\MixinFaceBakery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */