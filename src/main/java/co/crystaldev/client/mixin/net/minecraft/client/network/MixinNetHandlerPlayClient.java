package co.crystaldev.client.mixin.net.minecraft.client.network;

import co.crystaldev.client.event.impl.network.ChatReceivedEvent;
import co.crystaldev.client.event.impl.player.PlayerEvent;
//import co.crystaldev.client.handler.ClientCommandHandler;
import co.crystaldev.client.handler.ClientCommandHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.network.play.server.MixinS02PacketChat;
//import co.crystaldev.client.network.plugin.ChannelRegistry;
import co.crystaldev.client.network.plugin.ChannelRegistry;
import com.google.common.collect.ObjectArrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.*;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wdl.WDL;
import wdl.WDLHooks;
//import wdl.WDL;
//import wdl.WDLHooks;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mixin({NetHandlerPlayClient.class})
public abstract class MixinNetHandlerPlayClient {
  @Final
  @Shadow
  private static Logger logger;
  
  @ModifyArg(method = {"handleJoinGame", "handleRespawn"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
  private GuiScreen skipTerrainScreen(GuiScreen original) {
    return null;
  }
  
  @Redirect(method = {"handleUpdateSign"}, slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=Unable to locate sign at "}, ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
  private void disableSignDebugMessage(EntityPlayerSP instance, IChatComponent component) {}
  
  @ModifyArg(method = {"handleTabComplete"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;onAutocompleteResponse([Ljava/lang/String;)V", ordinal = 0))
  public String[] handleTabComplete(String[] in) {
    String[] complete = (ClientCommandHandler.getInstance()).latestAutoComplete;
    if (complete != null)
      //in = (String[])ObjectArrays.concat((Object[])complete, (Object[])in, String.class);
      in = ObjectArrays.concat(complete, in, String.class);

    return in;
  }
  
  @Inject(method = {"handleDisconnect"}, at = {@At("HEAD")})
  private void handleDisconnect(S40PacketDisconnect packetIn, CallbackInfo ci) {
    if (WDL.downloading) {
      WDL.stopDownload();
      try {
        Thread.sleep(2000L);
      } catch (Exception exception) {}
    }
  }
  
  @Inject(method = {"onDisconnect"}, at = {@At("HEAD")})
  private void onDisconnect(IChatComponent reason, CallbackInfo ci) {
    if (WDL.downloading) {
      WDL.stopDownload();
      try {
        Thread.sleep(2000L);
      } catch (Exception exception) {}
    }
  }
  
  @Inject(method = {"handleBlockAction"}, at = {@At("TAIL")})
  private void handleBlockAction(S24PacketBlockAction packetIn, CallbackInfo ci) {
    WDLHooks.onNHPCHandleBlockAction((NetHandlerPlayClient)(Object)this, packetIn);
  }
  
  @Inject(method = {"handleMaps"}, at = {@At("TAIL")})
  private void handleMaps(S34PacketMaps packetIn, CallbackInfo ci) {
    WDLHooks.onNHPCHandleMaps((NetHandlerPlayClient)(Object)this, packetIn);
  }
  
  @Inject(method = {"handleChat"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V", shift = At.Shift.AFTER)})
  private void handleChat(S02PacketChat packetIn, CallbackInfo ci) {
    ChatReceivedEvent event = new ChatReceivedEvent(packetIn.getChatComponent(), packetIn.getType());
    event.call();
    if (event.isCancelled()) {
      ci.cancel();
      return;
    } 
    ((MixinS02PacketChat)packetIn).setChatComponent(event.message);
    ((MixinS02PacketChat)packetIn).setType(event.getType());
  }
  
  @Inject(method = {"handlePlayerListItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V", shift = At.Shift.AFTER)})
  private void onPlayerDisconnect(S38PacketPlayerListItem packetIn, CallbackInfo ci) {
    if ((Minecraft.getMinecraft()).theWorld == null)
      return;
//    S38PacketPlayerListItem.Action action = packetIn.func_179768_b();

    S38PacketPlayerListItem.Action action = packetIn.getAction();
    List<EntityPlayer> players = (Minecraft.getMinecraft()).theWorld.playerEntities;
    for (S38PacketPlayerListItem.AddPlayerData data : packetIn.getEntries()) {
//    for (S38PacketPlayerListItem.AddPlayerData data : packetIn.func_179767_a()) {
      if (action == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
        for (EntityPlayer player : players) {
          if (player.getUniqueID().equals(data.getProfile().getId()))
            (new PlayerEvent.LoggedOut(player)).call();
        }
        continue;
      }
      if (action == S38PacketPlayerListItem.Action.ADD_PLAYER)
        for (EntityPlayer player : players) {
          if (player.getUniqueID().equals(data.getProfile().getId()))
            (new PlayerEvent.LoggedIn(player)).call();
        }
    }
  }
  
  @Inject(method = {"handleCustomPayload"}, at = {@At("TAIL")})
  public void handleCustomPayload(S3FPacketCustomPayload packetIn, CallbackInfo ci) {
    switch (packetIn.getChannelName()) {
      case "MC|TrList":
      case "MC|Brand":
      case "MC|BOpen":
        break;
      default:
        ChannelRegistry.getInstance().processPacket(packetIn);
        break;
    }
    packetIn.getBufferData().release();
  }
  
  @Inject(method = {"handleResourcePack"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S48PacketResourcePackSend;getHash()Ljava/lang/String;", shift = At.Shift.AFTER)})
  private void handleResourcePack(S48PacketResourcePackSend packetIn, CallbackInfo ci) {
    if (!validateResourcePackUrl(packetIn.getURL(), packetIn.getHash()))
      ci.cancel(); 
  }
  
  private boolean validateResourcePackUrl(String url, String hash) {
    try {
      URI uri = new URI(url);
      String scheme = uri.getScheme();
      boolean isLevelProtocol = "level".equals(scheme);
      if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol) {
        getNetworkManager().sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
        throw new URISyntaxException(url, "Wrong protocol");
      } 
      url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());
      if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
        logger.error("Malicious server tried to access " + url);
        throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
      } 
      return true;
    } catch (URISyntaxException|java.io.UnsupportedEncodingException ex) {
      return false;
    } 
  }
  
  @Shadow
  public abstract NetworkManager getNetworkManager();
}
