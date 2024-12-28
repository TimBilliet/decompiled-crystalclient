package co.crystaldev.client.mixin.optifine.net.minecraft.client.renderer;

import co.crystaldev.client.feature.impl.all.Fullbright;
import co.crystaldev.client.feature.impl.all.Zoom;
import co.crystaldev.client.util.Reflector;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
  @Redirect(method = {"getFOVModifier"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z", opcode = 181, ordinal = 0))
  @Dynamic("OptiFine")
  private void cancelSmoothCameraAndHandleZoom(GameSettings instance, boolean value) {
    if (!(Zoom.getInstance()).removeCinematic)
      instance.smoothCamera = value;
    Zoom.getInstance().resetZoomState();
  }

  @ModifyConstant(method = {"getFOVModifier"}, constant = {@Constant(floatValue = 4.0F)})
  @Dynamic("OptiFine")
  private float handleScrollZoom(float originalDivisor) {
    return Zoom.getInstance().getScrollZoomModifier();
  }

  @ModifyVariable(method = {"getFOVModifier"}, name = {"zoomActive"}, at = @At(value = "LOAD", ordinal = 0))
  @Dynamic("OptiFine")
  private boolean handleZoomStateChanged(boolean zoomActive) {
    Zoom.getInstance().handleZoomStateChange(zoomActive);
    return zoomActive;
  }

  @Redirect(method = {"getFOVModifier"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;isKeyDown(Lnet/minecraft/client/settings/KeyBinding;)Z"))
  @Dynamic("OptiFine")
  private boolean handleToggleToZoom(KeyBinding zoomKey) {
    return GameSettings.isKeyDown(zoomKey);
  }

  @ModifyVariable(method = {"getFOVModifier"}, name = {"f"}, at = @At(value = "FIELD", target = "Lnet/optifine/reflect/Reflector;ForgeHooksClient_getFOVModifier:Lnet/optifine/reflect/ReflectorMethod;", opcode = 178, ordinal = 0, remap = false))
  @Dynamic("OptiFine")
  private float handleSmoothZoom(float f) {
    float modifier = (Zoom.getInstance()).smoothZoom ? Zoom.getInstance().getSmoothZoomModifier() : 1.0F;
    return f * modifier;
  }

  @Redirect(method = {"updateLightmap"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Config;isCustomColors()Z"))
  @Dynamic("OptiFine")
  private boolean isCustomColorsOverride() {
    return (!(Fullbright.getInstance()).enabled && Reflector.Config$isCustomColors());
  }
}
