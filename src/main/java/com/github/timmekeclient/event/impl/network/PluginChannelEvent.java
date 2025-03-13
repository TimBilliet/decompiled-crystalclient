package com.github.timmekeclient.event.impl.network;

import com.github.timmekeclient.event.Event;
import com.github.timmekeclient.network.plugin.MessageHandler;
import net.minecraft.network.PacketBuffer;

public class PluginChannelEvent extends Event {
    public static class Register extends PluginChannelEvent {
    }

    public static class MessageReceived extends PluginChannelEvent {
        private final MessageHandler handler;

        private final PacketBuffer buffer;

        public MessageReceived(MessageHandler handler, PacketBuffer buffer) {
            this.handler = handler;
            this.buffer = buffer;
        }

        public MessageHandler getHandler() {
            return this.handler;
        }

        public PacketBuffer getBuffer() {
            return this.buffer;
        }
    }
}
