package co.crystaldev.client.mixin.net.minecraft.util;

import co.crystaldev.client.event.impl.player.MovementInputUpdateEvent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MovementInputFromOptions.class})
public abstract class MixinMovementUpdateFromOptions extends MovementInput{
  @Inject(method = {"updatePlayerMoveState"}, at = {@At("TAIL")})
  public void updatePlayerMoveState(CallbackInfo ci) {
    (new MovementInputUpdateEvent(this)).call();
  }
}

