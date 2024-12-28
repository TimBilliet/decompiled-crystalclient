package co.crystaldev.client.mixin.net.minecraft.entity.item;

import co.crystaldev.client.duck.EntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityTNTPrimed.class})
public abstract class MixinEntityTNTPrimed extends Entity {
  public MixinEntityTNTPrimed(World worldIn) {
    super(worldIn);
  }
  
  @Inject(method = {"<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/EntityLivingBase;)V"}, at = {@At("RETURN")})
  private void constructor(CallbackInfo ci) {
    ((EntityExt)this).setInitialYLevel(this.posY);
  }
  
  @Redirect(method = {"onUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityTNTPrimed;handleWaterMovement()Z"))
  private boolean onUpdate(EntityTNTPrimed entityTNTPrimed) {
    if (!entityTNTPrimed.worldObj.isRemote)
      return entityTNTPrimed.handleWaterMovement(); 
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\entity\item\MixinEntityTNTPrimed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */