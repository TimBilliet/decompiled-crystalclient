package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.event.impl.render.RenderBlockHighlightEvent;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.feature.impl.all.ClearWater;
import co.crystaldev.client.feature.impl.all.Fullbright;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.feature.impl.mechanic.PerspectiveMod;
import co.crystaldev.client.feature.impl.mechanic.WeatherChanger;
import co.crystaldev.client.handler.OverlayHandler;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
  @Shadow
  private Minecraft mc;
  
  @Redirect(method = {"setupCameraTransform"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
  private boolean cancelNauseaRender(EntityPlayerSP instance, Potion potion) {
    return (NoLag.isDisabled((NoLag.getInstance()).disableNausea) && instance.isPotionActive(potion));
  }
  
  @Redirect(method = {"renderWorldPass"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawSelectionBox(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovingObjectPosition;IF)V"))
  private void RenderGlobal$drawSelectionBox(RenderGlobal renderGlobal, EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
    RenderBlockHighlightEvent event = new RenderBlockHighlightEvent(renderGlobal, player, movingObjectPositionIn, partialTicks);
    event.call();
    if (!event.isCancelled())
      renderGlobal.drawSelectionBox(player, movingObjectPositionIn, 0, partialTicks); 
  }
  
  @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;renderGlobal:Lnet/minecraft/client/renderer/RenderGlobal;", ordinal = 0, shift = At.Shift.AFTER)})
  private void onRenderWorldPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
    (new RenderWorldEvent.Pre(this.mc.renderGlobal, partialTicks)).call();
  }
  
  @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.BY, by = -2)})
  private void onRenderWorldPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
    (new RenderWorldEvent.Post(this.mc.renderGlobal, partialTicks)).call();
  }
  
  @Redirect(method = {"orientCamera"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
  private MovingObjectPosition f5Fix(WorldClient instance, Vec3 vec3_1, Vec3 vec3_2) {
    return instance.rayTraceBlocks(vec3_1, vec3_2, false, true, false);
  }
  
  @Inject(method = {"renderWorldPass"}, slice = {@Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;"))}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0)})
  private void enablePolygonOffset(CallbackInfo ci) {
    GlStateManager.enablePolygonOffset();
    GlStateManager.doPolygonOffset(-0.325F, -0.325F);
  }
  
  @Inject(method = {"renderWorldPass"}, slice = {@Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;TRANSLUCENT:Lnet/minecraft/util/EnumWorldBlockLayer;"))}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/EnumWorldBlockLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 0, shift = At.Shift.AFTER)})
  private void disablePolygonOffset(CallbackInfo ci) {
    GlStateManager.disablePolygonOffset();
  }
  
  @Inject(method = {"updateCameraAndRender"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 1, shift = At.Shift.BEFORE)})
  public void updateCameraAndRender(float partialTicks, long nanoTime, CallbackInfo ci) {
    (new RenderOverlayEvent.Gui(this.mc.currentScreen, partialTicks)).call();
    if (OverlayHandler.getInstance() != null)
      OverlayHandler.getInstance().drawScreen(partialTicks);
  }
  
  @Redirect(method = {"getFOVModifier"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getMaterial()Lnet/minecraft/block/material/Material;"))
  private Material getFOVModifier(Block instance) {
    return ((ClearWater.getInstance()).enabled && (ClearWater.getInstance()).disableWaterFOV) ? Material.air : instance.getMaterial();
  }
  
  @Redirect(method = {"setupFog"}, at = @At(value = "FIELD", target = "Lnet/minecraft/block/material/Material;water:Lnet/minecraft/block/material/Material;", opcode = 178))
  public Material disableWaterFog() {
    if((ClearWater.getInstance() == null) ){
      return  Material.water;
    }
    if ((ClearWater.getInstance()).enabled && (ClearWater.getInstance()).disableWaterFog)
      return null;

    return Material.water;
  }

  @Redirect(method = {"setupFog"}, at = @At(value = "FIELD", target = "Lnet/minecraft/block/material/Material;lava:Lnet/minecraft/block/material/Material;", opcode = 178))
  public Material disableLavaFog() {
    if ((ClearWater.getInstance()).enabled && (ClearWater.getInstance()).disableLavaFog)
      return null;
    return Material.lava;
  }

  @Redirect(method = {"renderRainSnow"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/BiomeGenBase;canRain()Z"))
  public boolean renderRainSnow$canRain(BiomeGenBase biomeGenBase) {
    if ((WeatherChanger.getInstance()).enabled)
      return true;

    return biomeGenBase.canRain();
//    return biomeGenBase.canSpawnLightningBolt();
  }

  @Redirect(method = {"renderRainSnow"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/WorldChunkManager;getTemperatureAtHeight(FI)F"))
  public float renderRainSnow$getTemperatureAtHeight(WorldChunkManager worldChunkManager, float p_76939_1_, int p_76939_2_) {
    if ((WeatherChanger.getInstance()).enabled) {
      switch (WeatherChanger.getInstance().getState()) {
        case RAIN:
        case STORMING:
          return 0.15F;
      }
      return 0.0F;
    }
    return worldChunkManager.getTemperatureAtHeight(p_76939_1_, p_76939_2_);
  }
  
  @Redirect(method = {"renderRainSnow"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;color(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;"))
  public WorldRenderer renderRainSnow$WorldRenderer$color(WorldRenderer worldRenderer, float red, float green, float blue, float alpha) {
    if ((WeatherChanger.getInstance()).enabled) {
      Color color = (WeatherChanger.getInstance()).colorModifier.isChroma() ? RenderUtils.getCurrentChromaColor() : (Color)(WeatherChanger.getInstance()).colorModifier;
      return worldRenderer.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha);
    }
    return worldRenderer.color(red, green, blue, alpha);
  }

  @Inject(method = {"addRainParticles"}, cancellable = true, at = {@At("HEAD")})
  public void addRainParticles(CallbackInfo ci) {
    if ((WeatherChanger.getInstance()).enabled && WeatherChanger.getInstance().getState() == WeatherChanger.State.SNOW)
      ci.cancel();
  }

  @Redirect(method = {"updateCameraAndRender"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;inGameHasFocus:Z", opcode = 180))
  private boolean Minecraft$inGameHasFocus(Minecraft minecraft) {
    return (PerspectiveMod.getInstance() != null && PerspectiveMod.getInstance().overrideMouse());
  }

  @Redirect(method = {"orientCamera"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F", opcode = 180))
  private float orientCamera$rotationYaw(Entity entity) {
    return (PerspectiveMod.getInstance() != null && (PerspectiveMod.getInstance()).perspectiveToggled) ? (PerspectiveMod.getInstance()).cameraYaw : entity.rotationYaw;
  }

  @Redirect(method = {"orientCamera"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F", opcode = 180))
  private float orientCamera$rotationPitch(Entity entity) {
    return (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraPitch : entity.rotationPitch;
  }

  @Redirect(method = {"orientCamera"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F", opcode = 180))
  private float orientCamera$prevRotationYaw(Entity entity) {
    return (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraYaw : entity.prevRotationYaw;
  }

  @Redirect(method = {"orientCamera"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F", opcode = 180))
  private float orientCamera$prevRotationPitch(Entity entity) {
    return (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraPitch : entity.prevRotationPitch;
  }

  @Redirect(method = {"updateLightmap"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F", opcode = 180))
  public float updateLightmap(GameSettings gameSettings) {
    return (Fullbright.getInstance()).enabled ? 100.0F : gameSettings.gammaSetting;
  }
  
  @Inject(method = {"hurtCameraEffect"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;", ordinal = 1, shift = At.Shift.BEFORE)})
  private void revertCameraShake(float partialTicks, CallbackInfo ci) {
    if ((OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertCameraShake) {
      ci.cancel();
      EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
      float var3 = entitylivingbase.hurtTime - partialTicks;
      if (entitylivingbase.getHealth() <= 0.0F) {
        float var4 = entitylivingbase.deathTime + partialTicks;
        GL11.glRotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
      }
      if (var3 >= 0.0F) {
        var3 /= entitylivingbase.maxHurtTime;
        var3 = MathHelper.sin(var3 * var3 * var3 * var3 * 3.1415927F);
        float var4 = entitylivingbase.attackedAtYaw;
        GL11.glRotatef(-var4, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(var4, 0.0F, 1.0F, 0.0F);
      }
    }
  }
}
