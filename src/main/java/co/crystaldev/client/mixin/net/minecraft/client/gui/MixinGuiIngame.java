package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
import co.crystaldev.client.gui.screens.override.ScreenPlayerTabOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public abstract class MixinGuiIngame {
  @Final
  @Shadow
  protected Minecraft mc;
  
  @Mutable
  @Shadow
  @Final
  protected GuiPlayerTabOverlay overlayPlayerList;
  
  @Inject(method = {"<init>"}, at = {@At("RETURN")})
  public void injectCustomTabOverlay(Minecraft mcIn, CallbackInfo ci) {
    this.overlayPlayerList = new ScreenPlayerTabOverlay(mcIn, (GuiIngame)(Object)this);
  }
  
  @Inject(method = {"renderScoreboard"}, cancellable = true, at = {@At("HEAD")})
  private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledResolution, CallbackInfo ci) {
    ci.cancel();
  }
  
  @Inject(method = {"renderBossHealth"}, cancellable = true, at = {@At(value = "FIELD", target = "Lnet/minecraft/entity/boss/BossStatus;statusBarTime:I", ordinal = 1, shift = At.Shift.BEFORE)})
  private void renderBossHealth(CallbackInfo ci) {
    RenderOverlayEvent.BossBar event = new RenderOverlayEvent.BossBar((Client.getTimer()).renderPartialTicks);
    event.call();
    if (event.isCancelled())
      ci.cancel(); 
  }
  
  @ModifyVariable(method = {"renderPlayerStats(Lnet/minecraft/client/gui/ScaledResolution;)V"}, at = @At(value = "STORE", ordinal = 0), ordinal = 1)
  private GuiIngame oldAnimations$disableHealthFlash(GuiIngame value) {
    return ((OldAnimations.getInstance()).enabled && (OldAnimations.getInstance()).revertHealthFlash) ? value : value;
  }
}
