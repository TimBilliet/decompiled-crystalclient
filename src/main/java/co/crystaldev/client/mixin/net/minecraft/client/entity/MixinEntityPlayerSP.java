package co.crystaldev.client.mixin.net.minecraft.client.entity;

import co.crystaldev.client.event.Event;
import co.crystaldev.client.event.impl.entity.EntityCriticalStrikeEvent;
import co.crystaldev.client.event.impl.entity.EntityEnchantCriticalStrikeEvent;
import co.crystaldev.client.event.impl.player.PlayerChatEvent;
import co.crystaldev.client.event.impl.player.PlayerEvent;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityPlayerSP.class})
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
  @Shadow
  public float timeInPortal;
  
  @Shadow
  public float prevTimeInPortal;
  
  public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
    super(worldIn, playerProfile);
  }
  
  @Inject(method = {"sendChatMessage"}, cancellable = true, at = {@At("HEAD")})
  private void sendChatMessage(String message, CallbackInfo ci) {
    PlayerChatEvent event = new PlayerChatEvent((EntityPlayer)this, message);
    event.call();
    if (event.isCancelled())
      ci.cancel(); 
  }
  
  @Redirect(method = {"onLivingUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
  private void portalFix(Minecraft minecraft, GuiScreen guiScreenIn) {}

  @Redirect(method = {"onLivingUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isPotionActive(Lnet/minecraft/potion/Potion;)Z", ordinal = 0))
  private boolean cancelNauseaRendering(EntityPlayerSP instance, Potion potion) {
    return (NoLag.isDisabled((NoLag.getInstance()).disableNausea) && instance.isPotionActive(potion));
  }
  
  @Inject(method = {"damageEntity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setHealth(F)V", shift = At.Shift.BEFORE)})
  public void onDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci) {
    (new PlayerEvent.Damage((EntityPlayer)this, damageSrc, getHealth() - damageAmount, damageAmount)).call();
  }
  
  @Inject(method = {"onCriticalHit"}, at = {@At("HEAD")}, cancellable = true)
  public void onCriticalHit(Entity entityHit, CallbackInfo ci) {
    Event event = (new EntityCriticalStrikeEvent(entityHit)).call();
    if (event.isCancelled())
      ci.cancel(); 
  }
  
  @Inject(method = {"onEnchantmentCritical"}, at = {@At("HEAD")}, cancellable = true)
  public void onEnchantmentCritical(Entity entityHit, CallbackInfo ci) {
    Event event = (new EntityEnchantCriticalStrikeEvent(entityHit)).call();
    if (event.isCancelled())
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\entity\MixinEntityPlayerSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */