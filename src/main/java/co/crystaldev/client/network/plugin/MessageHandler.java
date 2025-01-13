package co.crystaldev.client.network.plugin;


import co.crystaldev.client.network.ByteBufWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public abstract class MessageHandler {
    private String channelName;

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void sendMessage(String content) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null)
            mc.getNetHandler().addToSendQueue((Packet) new C17PacketCustomPayload(this.channelName, new PacketBuffer(Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)))));
    }

    public void sendMessage(ByteBufWrapper wrapper) {
        sendMessage(wrapper.buf());
    }

    public void sendMessage(ByteBuf buf) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null)
            mc.getNetHandler().addToSendQueue((Packet) new C17PacketCustomPayload(this.channelName, new PacketBuffer(Unpooled.wrappedBuffer(buf.array()))));
    }

    public abstract void fromBytes(ByteBuf paramByteBuf);

    public abstract void toBytes(ByteBuf paramByteBuf);

    public abstract void onMessage();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\plugin\MessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */