package co.crystaldev.client.mixin.net.minecraft.entity.player;

import co.crystaldev.client.event.impl.player.PlayerEvent;
import co.crystaldev.client.event.impl.tick.PlayerTickEvent;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.entity.MixinEntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends Entity {
  private float currentHeight;

  private long lastMillis;

  public MixinEntityPlayer() {
    super(null);
    this.currentHeight = 1.62F;
    this.lastMillis = System.currentTimeMillis();
  }

  @Shadow
  public abstract boolean isPlayerSleeping();

  @Inject(method = {"attackTargetEntityWithCurrentItem"}, at = {@At("HEAD")})
  private void attackTargetEntityWithCurrentItem(Entity targetEntity, CallbackInfo ci) {
    (new PlayerEvent.Attack((EntityPlayer)(Object)this, targetEntity)).call();
  }

  @Inject(method = {"onUpdate"}, at = {@At("HEAD")})
  private void onPlayerTickPre(CallbackInfo ci) {
    (new PlayerTickEvent.Pre((EntityPlayer)(Object)this)).call();
  }

  @Inject(method = {"onUpdate"}, at = {@At("TAIL")})
  private void onPlayerTickPost(CallbackInfo ci) {
    (new PlayerTickEvent.Post((EntityPlayer)(Object)this)).call();
  }

  @Inject(method = {"getEyeHeight"}, cancellable = true, at = {@At("HEAD")})
  public void getEyeHeight(CallbackInfoReturnable<Float> cir) {
    if (OldAnimations.getInstance() != null && (OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertSneaking) {
      if (isPlayerSleeping()) {
        cir.setReturnValue(0.2F);
        return;
      }
      if (isSneaking()) {
        if (this.currentHeight > 1.54F) {
          long time = System.currentTimeMillis();
          if (time - this.lastMillis > 10L) {
            this.currentHeight -= 0.012F;
            this.lastMillis = time;
          }
        }
      } else if (this.currentHeight < 1.62F && this.currentHeight > 0.2F) {
        long time = System.currentTimeMillis();
        if (time - this.lastMillis > 10L) {
          this.currentHeight += 0.012F;
          this.lastMillis = time;
        }
      } else {
        this.currentHeight = 1.62F;
      }
      cir.setReturnValue(this.currentHeight);
    }
  }
}
