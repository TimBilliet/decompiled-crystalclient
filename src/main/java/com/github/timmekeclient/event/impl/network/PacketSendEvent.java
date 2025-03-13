package com.github.timmekeclient.event.impl.network;

import com.github.timmekeclient.event.Cancellable;
import com.github.timmekeclient.event.Event;
import net.minecraft.network.Packet;

public class PacketSendEvent extends Event {
    public Packet packet;

    private PacketSendEvent(Packet packet) {
        this.packet = packet;
    }

    @Cancellable
    public static class Pre extends PacketSendEvent {
        public Pre(Packet packet) {
            super(packet);
        }
    }

    public static class Post extends PacketSendEvent {
        public Post(Packet packet) {
            super(packet);
        }
    }
}
