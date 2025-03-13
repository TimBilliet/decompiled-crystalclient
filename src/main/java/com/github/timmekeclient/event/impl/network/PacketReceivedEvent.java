package com.github.timmekeclient.event.impl.network;

import com.github.timmekeclient.event.Cancellable;
import com.github.timmekeclient.event.Event;
import net.minecraft.network.Packet;

public class PacketReceivedEvent extends Event {
    public Packet packet;

    private PacketReceivedEvent(Packet packet) {
        this.packet = packet;
    }

    @Cancellable
    public static class Pre extends PacketReceivedEvent {
        public Pre(Packet packet) {
            super(packet);
        }
    }

    public static class Post extends PacketReceivedEvent {
        public Post(Packet packet) {
            super(packet);
        }
    }
}
