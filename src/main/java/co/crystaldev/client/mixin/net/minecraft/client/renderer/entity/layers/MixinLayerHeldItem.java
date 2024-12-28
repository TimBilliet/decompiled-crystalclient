package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity.layers;

import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.entity.MixinRendererLivingEntity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerHeldItem.class})
public abstract class MixinLayerHeldItem {
  @Shadow
  @Final
  private RendererLivingEntity<?> livingEntityRenderer;
  
  @Inject(method = {"doRenderLayer"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", shift = At.Shift.BEFORE)})
  public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale, CallbackInfo ci) {
    ModelBiped model = (ModelBiped)((MixinRendererLivingEntity)this.livingEntityRenderer).getMainModel();
    boolean isBlocking = (model.heldItemRight == 3);
    if (isBlocking && (OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertBlocking) {
      GlStateManager.rotate(-45.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(20.0F, 1.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\layers\MixinLayerHeldItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */