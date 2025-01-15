package co.crystaldev.client.mixin.net.minecraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ModelPlayer.class})
public abstract class MixinModelPlayer extends ModelBiped {
    @Shadow
    private boolean smallArms;

    @ModifyConstant(method = {"<init>"}, constant = {@Constant(floatValue = 2.5F)})
    private float fixAlexArmPos(float original) {
        return 2.0F;
    }


    /**
     * @author
     */
    @Overwrite
    public void postRenderArm(float scale) {
        if (this.smallArms) {
            this.bipedRightArm.rotationPointX += 0.5F;
            this.bipedRightArm.postRender(scale);
            this.bipedRightArm.rotationPointZ -= 0.5F;
        } else {
            this.bipedRightArm.postRender(scale);
        }
    }
}
