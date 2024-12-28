package co.crystaldev.client.mixin.net.minecraft.entity;

import co.crystaldev.client.Client;
import co.crystaldev.client.duck.EntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class MixinEntity implements EntityExt {
  @Unique
  private final int crystal$crystalId = incCrystalId();

  @Unique
  private boolean crystal$shouldRenderNametag = true;

  @Unique
  private double crystal$initialYLevel = 0.0D;

  @Shadow
  public boolean onGround;

  @Shadow
  public abstract boolean isInWater();

  public int getCrystalEntityId() {
    return this.crystal$crystalId;
  }

  public double getInitialYLevel() {
    return this.crystal$initialYLevel;
  }

  public void setInitialYLevel(double newY) {
    this.crystal$initialYLevel = newY;
  }

  public boolean isShouldRenderNametag() {
    return this.crystal$shouldRenderNametag;
  }

  public void setShouldRenderNametag(boolean shouldRenderNametag) {
    this.crystal$shouldRenderNametag = shouldRenderNametag;
  }

  /**
   * @author Tim
   */
  @Overwrite(aliases = {"isWet"})
  public boolean isWet() {
    Entity entity;
    return (isInWater() || ((entity = (Entity)(Object) this).worldObj.isThundering() && entity.worldObj.isRaining()));
  }

  @Redirect(method = {"getBrightnessForRender"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/BlockPos;)Z"))
  public boolean alwaysGetBrightness(World world, BlockPos pos) {
    return true;
  }

  @Inject(method = {"spawnRunningParticles"}, at = {@At("HEAD")}, cancellable = true)
  private void checkGroundState(CallbackInfo ci) {
    if (!this.onGround)
      ci.cancel();
  }

  private int incCrystalId() {
    Object o = this;
    return (o instanceof net.minecraft.entity.item.EntityTNTPrimed || o instanceof net.minecraft.entity.item.EntityFallingBlock) ?
      Client.getInstance().getEntityCounter().getAndIncrement() : -1;
  }
}
