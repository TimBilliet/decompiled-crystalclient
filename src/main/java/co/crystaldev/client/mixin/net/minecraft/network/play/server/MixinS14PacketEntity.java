package co.crystaldev.client.mixin.net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({S14PacketEntity.class})
public abstract class MixinS14PacketEntity {
  @Inject(method = {"getEntity"}, at = {@At("HEAD")}, cancellable = true)
  public void getEntity(World worldIn, CallbackInfoReturnable<Entity> ci) {
    if (worldIn == null)
      ci.setReturnValue(null); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\network\play\server\MixinS14PacketEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */