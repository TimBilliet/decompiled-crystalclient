package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.duck.EntityArmorStandExt;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorStandRenderer.class})
public abstract class MixinArmorStandRenderer {
  @Inject(method = {"canRenderName*"}, cancellable = true, at = {@At("HEAD")})
  private void canRenderName(EntityArmorStand entity, CallbackInfoReturnable<Boolean> ci) {
    if (NoLag.isEnabled((NoLag.getInstance()).disableHologramsInBlocks)) {
      EntityArmorStandExt stand = (EntityArmorStandExt)entity;
      long currentMs = System.currentTimeMillis();
      if (currentMs - stand.crystal$getLastBlockCheck() < 10000L) {
        ci.setReturnValue(!stand.crystal$isInBlock());
      } else {
        boolean inBlock = !entity.getEntityWorld().isAirBlock(entity.getPosition());
        stand.crystal$setIsInBlock(inBlock);
        stand.crystal$setLastBlockCheck(currentMs);
        ci.setReturnValue(!inBlock);
        return;
      }
    }
    if ((NoLag.getInstance()).enabled && entity.getDistanceToEntity((Entity)(Minecraft.getMinecraft()).thePlayer) > (NoLag.getInstance()).hologramRenderDistance)
      ci.setReturnValue(Boolean.FALSE);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinArmorStandRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */