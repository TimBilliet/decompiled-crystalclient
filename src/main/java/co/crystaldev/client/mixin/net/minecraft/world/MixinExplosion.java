package co.crystaldev.client.mixin.net.minecraft.world;

import co.crystaldev.client.event.impl.world.ExplosionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Explosion.class})
public abstract class MixinExplosion {
  @Final
  @Shadow
  private World worldObj;
  
  @Final
  @Shadow
  private double explosionX;
  
  @Final
  @Shadow
  private double explosionY;
  
  @Final
  @Shadow
  private double explosionZ;
  
  @Final
  @Shadow
  private Entity exploder;
  
  @Final
  @Shadow
  private float explosionSize;
  
  @Final
  @Shadow
  private boolean isSmoking;
  
  @Final
  @Shadow
  private boolean isFlaming;
  
  @Inject(method = {"doExplosionB"}, at = {@At("HEAD")})
  private void doExplosionB(CallbackInfo ci) {
    (new ExplosionEvent(this.worldObj, this.exploder, this.explosionX, this.explosionY, this.explosionZ, this.explosionSize, this.isSmoking, this.isFlaming)).call();
  }
}

