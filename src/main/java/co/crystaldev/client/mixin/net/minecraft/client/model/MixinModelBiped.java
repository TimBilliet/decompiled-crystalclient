package co.crystaldev.client.mixin.net.minecraft.client.model;

import co.crystaldev.client.feature.impl.combat.OldAnimations;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBiped.class})
public abstract class MixinModelBiped {
    @Shadow
    public ModelRenderer bipedRightArm;

    @Inject(method = {"setRotationAngles"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelRenderer;rotateAngleY:F", ordinal = 6, shift = At.Shift.AFTER)})
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if ((OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertBlocking)
            this.bipedRightArm.rotateAngleY = 0.0F;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\model\MixinModelBiped.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */