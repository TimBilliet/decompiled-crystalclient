package co.crystaldev.client.network.plugin;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.network.PluginChannelEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class ChannelRegistry {
    private static ChannelRegistry INSTANCE;

    private final Map<String, MessageHandler> channels = Maps.newConcurrentMap();

    private long lastRegister = 0L;

    public ChannelRegistry() {
        INSTANCE = this;
    }

    public void newChannel(String name, MessageHandler handler) {
        if (name.length() > 20)
            throw new RuntimeException("Channel names cannot exceed 20 characters");
        if (this.channels.containsKey(name))
            throw new RuntimeException("That channel name is already registered");
        if (name.startsWith("MC|") || name.startsWith("FML") || name.startsWith("\001"))
            throw new RuntimeException("That channel name cannot be registered as it is either reserved or invalid");
        this.channels.put(name, handler);
        handler.setChannelName(name);
        registerChannel(name);
    }

    public void processPacket(S3FPacketCustomPayload packet) {
        if (this.channels.containsKey(packet.getChannelName())) {
            MessageHandler handler = this.channels.get(packet.getChannelName());
            handler.fromBytes(packet.getBufferData());
            handler.onMessage();
            (new PluginChannelEvent.MessageReceived(handler, packet.getBufferData())).call();
        } else {
            Reference.LOGGER.warn("Could not find a handler for registered channel \"" + packet.getChannelName() + "\"");
        }
    }

    private void registerChannel(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null && !this.channels.isEmpty())
            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.wrappedBuffer(name.getBytes(StandardCharsets.UTF_8)))));
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null && System.currentTimeMillis() - this.lastRegister > 2000L) {
            String data = Joiner.on("\000").join(this.channels.keySet());
            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes(StandardCharsets.UTF_8)))));
            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("CC|Init", new PacketBuffer(Unpooled.wrappedBuffer("crystalclient_1.1.12".getBytes(StandardCharsets.UTF_8)))));
            this.lastRegister = System.currentTimeMillis();
            (new PluginChannelEvent.Register()).call();
        }
    }

    public static ChannelRegistry getInstance() {
        return INSTANCE;
    }
}
