package co.crystaldev.client.mixin.net.minecraft.entity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin({EntityLivingBase.class})
public abstract class MixinEntityLivingBase {
    @Redirect(method = {"onLivingUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isInWater()Z"))
    private boolean EntityLivingBase$isInWater(EntityLivingBase entityLivingBase) {
        if (entityLivingBase instanceof EntityPlayer)
            return (!((EntityPlayer) entityLivingBase).capabilities.isFlying && entityLivingBase.isInWater());
        return entityLivingBase.isInWater();
    }

    @Redirect(method = {"onLivingUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isInLava()Z"))
    private boolean EntityLivingBase$isInLava(EntityLivingBase entityLivingBase) {
        if (entityLivingBase instanceof EntityPlayer)
            return (!((EntityPlayer) entityLivingBase).capabilities.isFlying && entityLivingBase.isInLava());
        return entityLivingBase.isInLava();
    }

    @Inject(method = {"updatePotionEffects"}, locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;onUpdate(Lnet/minecraft/entity/EntityLivingBase;)Z")})
    private void EntityLivingBase$checkPotionEffect(CallbackInfo ci, Iterator<Integer> iterator, Integer integer, PotionEffect potioneffect) {
        if (potioneffect == null)
            ci.cancel();
    }

    @Inject(method = {"updatePotionEffects"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V")})
    private void cleanDisplay(CallbackInfo ci) {
        if (NoLag.isDisabled((NoLag.getInstance()).showOwnParticles) && (Object) this == (Minecraft.getMinecraft()).thePlayer)
            ci.cancel();
    }

    @Inject(method = {"getLook"}, cancellable = true, at = {@At("HEAD")})
    public void getLook(float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (partialTicks != 1.0F && self instanceof EntityPlayer)
            cir.setReturnValue(self.getLook(1.0F));
    }
}
