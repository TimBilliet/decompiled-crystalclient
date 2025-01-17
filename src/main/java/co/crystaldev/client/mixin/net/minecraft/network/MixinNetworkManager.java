package co.crystaldev.client.mixin.net.minecraft.network;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.network.PacketSendEvent;
import co.crystaldev.client.util.CallbackClickEvent;
import co.crystaldev.client.util.enums.ChatColor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

@Mixin({NetworkManager.class})
public abstract class MixinNetworkManager {
    @Shadow
    @Final
    private static Logger logger;

    /**
     * @author
     */
    @Overwrite(aliases = {"exceptionCaught"})
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        if (throwable instanceof io.netty.handler.timeout.TimeoutException) {
            ChatComponentTranslation component = new ChatComponentTranslation("disconnect.timeout");
            closeChannel((IChatComponent) component);
            return;
        }
        logger.error("Exception caught in channel", throwable);
        ChatComponentText upload = new ChatComponentText(ChatColor.translate("&nClick Here&f"));
        upload.getChatStyle().setChatClickEvent((ClickEvent) new CallbackClickEvent(comp -> {
            try {
                //String link = Hastebin.upload(throwable + "\n" + (String)Stream.<StackTraceElement>of(throwable.getStackTrace()).map().collect(Collectors.joining("\n")));
//              String link = Hastebin.upload(throwable + "\n" + (String)Stream.<StackTraceElement>of(throwable.getStackTrace()).collect(Collectors.joining("\n")))
                String link = "dummylink";

                StringSelection selection = new StringSelection(link);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                Client.sendMessage("Uploaded & copied to clipboard", true);
                throw new IOException();
            } catch (IOException ex) {
                Client.sendErrorMessage("Unable to upload", true);
            }
        }));
        ChatComponentText disconnect = new ChatComponentText(ChatColor.translate("&nClick Here&f"));
        disconnect.getChatStyle().setChatClickEvent((ClickEvent) new CallbackClickEvent(comp -> closeChannel((IChatComponent) new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Internal Exception: " + throwable}))));
        ChatComponentText discord = new ChatComponentText(ChatColor.translate("&nClick Here&f"));
        discord.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/mmVWkk93E9"));
        ChatComponentText chatComponentText1 = new ChatComponentText(ChatColor.translate(Client.getErrorPrefix() + " An error occurred within the connection to the connected server (" + throwable + ")"));
        ChatComponentText chatComponentText2 = new ChatComponentText(ChatColor.translate("  &7&l* &fYour connection to the server may be unstable, this error should be reported"));
        IChatComponent opt1 = (new ChatComponentText(ChatColor.translate("  &7&l* &f"))).appendSibling((IChatComponent) upload).appendText(" to upload the error");
        IChatComponent opt2 = (new ChatComponentText(ChatColor.translate("  &7&l* &f"))).appendSibling((IChatComponent) disconnect).appendText(" to disconnect from the server");
        if ((Minecraft.getMinecraft()).thePlayer != null) {
            EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).thePlayer;
            entityPlayerSP.addChatMessage((IChatComponent) chatComponentText1);
            entityPlayerSP.addChatMessage((IChatComponent) chatComponentText2);
            entityPlayerSP.addChatMessage(opt1);
            entityPlayerSP.addChatMessage(opt2);
        }
    }

    @Inject(method = {"channelRead0"}, cancellable = true, at = {@At(value = "FIELD", target = "Lnet/minecraft/network/NetworkManager;packetListener:Lnet/minecraft/network/INetHandler;", opcode = 180, shift = At.Shift.BEFORE)})
    private void onPacketReceivedPre(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_, CallbackInfo ci) {
        PacketReceivedEvent.Pre pre = new PacketReceivedEvent.Pre(p_channelRead0_2_);
        pre.call();
        if (pre.isCancelled())
            ci.cancel();
    }

    @Inject(method = {"channelRead0"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/network/NetworkManager;packetListener:Lnet/minecraft/network/INetHandler;", opcode = 180, shift = At.Shift.BEFORE)})
    private void onPacketReceivedPost(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_, CallbackInfo ci) {
        (new PacketReceivedEvent.Post(p_channelRead0_2_)).call();
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, cancellable = true, at = {@At("HEAD")})
    private void onPacketSendPre(Packet packetIn, CallbackInfo ci) {
        PacketSendEvent.Pre event = new PacketSendEvent.Pre(packetIn);
        event.call();
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V"}, cancellable = true, at = {@At("HEAD")})
    private void onPacketSendPre(Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener[] listeners, CallbackInfo ci) {
        PacketSendEvent.Pre event = new PacketSendEvent.Pre(packetIn);
        event.call();
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("TAIL")})
    private void onPacketSendPost(Packet packetIn, CallbackInfo ci) {
        new PacketSendEvent.Post(packetIn);
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V"}, at = {@At("TAIL")})
    private void onPacketSendPost(Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener[] listeners, CallbackInfo ci) {
        new PacketSendEvent.Post(packetIn);
    }

    @Shadow
    public abstract void closeChannel(IChatComponent paramIChatComponent);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\network\MixinNetworkManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */