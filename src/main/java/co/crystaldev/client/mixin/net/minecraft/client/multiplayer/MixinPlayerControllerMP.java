package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import co.crystaldev.client.feature.impl.all.Farming;
import co.crystaldev.client.feature.impl.combat.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerControllerMP.class})
public abstract class MixinPlayerControllerMP {
  @Shadow
  @Final
  private Minecraft mc;
  
  @Shadow
  private boolean isHittingBlock;
  
  @Shadow
  @Final
  private NetHandlerPlayClient netClientHandler;
  
  @Inject(method = {"clickBlock"}, cancellable = true, at = {@At("HEAD")})
  private void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
    if (Farming.getInstance().onClickBlock(loc))
      cir.setReturnValue(Boolean.FALSE);
  }
  
  @Inject(method = {"onPlayerDamageBlock"}, cancellable = true, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;stepSoundTickCounter:F", shift = At.Shift.AFTER, ordinal = 1)})
  public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
    if ((OldAnimations.getInstance()).enabled && this.mc.playerController.gameIsSurvivalOrAdventure() &&
      (OldAnimations.getInstance()).punchDuringUsage) {
      ItemStack stack = this.mc.thePlayer.getHeldItem();
      int id;
      if (stack == null || (id = Item.getIdFromItem(stack.getItem())) == 332 || id == 381 || id == 368)
        return;
      if (this.mc.gameSettings.keyBindAttack.isPressed() && (this.mc.gameSettings.keyBindUseItem.isPressed() || this.mc.thePlayer.isUsingItem()) && this.mc.thePlayer.getHeldItem() != null && (this.mc.thePlayer.getHeldItem()).stackSize > 0 &&

      //if (this.mc.gameSettings.keyBindAttack.getIsKeyPressed() && (this.mc.gameSettings.keyBindUseItem.getIsKeyPressed() || this.mc.thePlayer.isUsingItem()) && this.mc.thePlayer.getHeldItem() != null && (this.mc.thePlayer.getHeldItem()).stackSize > 0 &&
        sendUseItem((EntityPlayer)this.mc.thePlayer, (World)this.mc.theWorld, this.mc.thePlayer.getHeldItem())) {
        this.isHittingBlock = false;
        this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
        cir.setReturnValue(Boolean.FALSE);
      }
    }
  }
  
  @Shadow
  public abstract boolean sendUseItem(EntityPlayer paramEntityPlayer, World paramWorld, ItemStack paramItemStack);
}
