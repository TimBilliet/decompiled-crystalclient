package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import net.minecraft.client.renderer.BlockFluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({BlockFluidRenderer.class})
public abstract class MixinBlockFluidRenderer {
    @ModifyConstant(method = {"renderFluid"}, constant = {@Constant(floatValue = 0.001F)})
    private float fixFluidStitching(float original) {
        return 0.0F;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinBlockFluidRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */