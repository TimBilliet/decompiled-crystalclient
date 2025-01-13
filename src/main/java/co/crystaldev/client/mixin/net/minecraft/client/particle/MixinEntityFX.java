package co.crystaldev.client.mixin.net.minecraft.client.particle;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityFX.class})
public abstract class MixinEntityFX extends Entity {
    public MixinEntityFX(World worldIn) {
        super(worldIn);
    }

    public void moveEntity(double x, double y, double z) {
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }

    @Redirect(method = {"renderParticle"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EntityFX;getBrightnessForRender(F)I"))
    private int staticParticleColor(EntityFX entityFX, float partialTicks) {
        return NoLag.isEnabled((NoLag.getInstance()).staticParticleColor) ? 15728880 : entityFX.getBrightnessForRender(partialTicks);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\particle\MixinEntityFX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */