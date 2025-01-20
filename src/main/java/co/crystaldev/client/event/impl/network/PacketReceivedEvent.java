package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
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
