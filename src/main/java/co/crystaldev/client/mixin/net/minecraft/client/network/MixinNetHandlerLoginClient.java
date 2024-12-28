package co.crystaldev.client.mixin.net.minecraft.client.network;

import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetHandlerLoginClient.class})
public abstract class MixinNetHandlerLoginClient {
  @Final
  @Shadow
  private NetworkManager networkManager;
  
  @Inject(method = {"handleLoginSuccess"}, at = {@At("TAIL")})
  public void onServerConnect(S02PacketLoginSuccess packetIn, CallbackInfo ci) {
    (new ServerConnectEvent(this.networkManager)).call();
  }
  
  @Inject(method = {"handleDisconnect"}, at = {@At("TAIL")})
  public void onServerDisconnect(S00PacketDisconnect packetIn, CallbackInfo ci) {
    (new ServerDisconnectEvent(this.networkManager)).call();
  }
}
