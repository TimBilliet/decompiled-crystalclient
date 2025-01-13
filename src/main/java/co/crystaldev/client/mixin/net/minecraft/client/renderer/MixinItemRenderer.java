package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.feature.impl.all.ClearWater;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
//import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ItemRenderer.class})
public abstract class MixinItemRenderer {
    @Shadow
    private ItemStack itemToRender;

    private boolean isFishingRod = false;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
//  protected abstract void func_178097_a(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3);
    protected abstract void renderItemMap(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3);

    @Shadow
//  protected abstract void func_178095_a(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2);
    protected abstract void renderPlayerArm(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2);

    @Shadow
    public abstract void renderItem(EntityLivingBase paramEntityLivingBase, ItemStack paramItemStack, ItemCameraTransforms.TransformType paramTransformType);

    @Shadow
//  protected abstract void func_178098_a(float paramFloat, AbstractClientPlayer paramAbstractClientPlayer);
    protected abstract void doBowTransformations(float paramFloat, AbstractClientPlayer paramAbstractClientPlayer);

    @Shadow
//  protected abstract void func_178103_d();
    protected abstract void doBlockTransformations();

    @Shadow
//  protected abstract void func_178104_a(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat);
    protected abstract void performDrinking(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat);

    @Shadow
//  protected abstract void func_178105_d(float paramFloat);
    protected abstract void doItemUsedTransformations(float paramFloat);

    @Inject(method = {"renderFireInFirstPerson"}, at = {@At("HEAD")}, cancellable = true)
    private void fireOverlayPre(float partialTicks, CallbackInfo ci) {
        if (this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1").getFrameCount() == 0) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float) (ClientOptions.getInstance()).fireOverlayHeight, 0.0F);
    }

//  @Shadow
//  protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Inject(method = {"renderFireInFirstPerson"}, at = {@At("TAIL")})
    private void fireOverlayPost(float partialTicks, CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @ModifyArg(method = {"renderWaterOverlayTexture"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 0), index = 3)
    private float renderWaterOverlayTexture(float x) {
        return (ClearWater.getInstance()).enabled ? (x * (ClearWater.getInstance()).overlayTransparency / 100.0F) : x;
    }

    @Inject(method = {"renderItemInFirstPerson"}, cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/ItemRenderer;itemToRender:Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE, ordinal = 0)})
    public void renderItemInFirstPerson(float partialTicks, CallbackInfo ci, float f, AbstractClientPlayer abstractclientplayer, float f1, float f2, float f3) {
        ci.cancel();
        if (this.itemToRender != null) {
            this.isFishingRod = (this.itemToRender.getItem() instanceof net.minecraft.item.ItemFishingRod && (OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertFishingRod);
            if (this.itemToRender.getItem() instanceof net.minecraft.item.ItemMap) {
                renderItemMap(abstractclientplayer, f2, f, f1);//func_178097_a
            } else if (abstractclientplayer.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();
                float f1Old = f1;
                switch (enumaction) {
                    case NONE:
                        transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        f1 = (OldAnimations.getInstance()).enabled ? f1 : 0.0F;
                        performDrinking(abstractclientplayer, partialTicks);
                        transformFirstPersonItem(f, f1);
                        break;
                    case BLOCK:
                        f1 = (OldAnimations.getInstance()).enabled ? f1 : 0.0F;
                        transformFirstPersonItem(f, f1);
                        doBlockTransformations();
                        break;
                    case BOW:
                        f1 = (OldAnimations.getInstance()).enabled ? f1Old : f1;
                        transformFirstPersonItem(f, f1);
                        doBowTransformations(partialTicks, abstractclientplayer);
                        break;
                }
            } else {
                doItemUsedTransformations(f1);
                transformFirstPersonItem(f, f1);
            }
            renderItem((EntityLivingBase) abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible()) {
            renderPlayerArm(abstractclientplayer, f, f1);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * @author
     */
    @Overwrite
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {//func_178096_b
        if (this.isFishingRod) {
            GlStateManager.translate(0.4F, -0.42F, -0.71999997F);
        } else {
            GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        }
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(this.isFishingRod ? 50.0F : 45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        if (this.isFishingRod) {
            GlStateManager.scale(0.3F, 0.3F, 0.3F);
        } else {
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
        }
    }
}
