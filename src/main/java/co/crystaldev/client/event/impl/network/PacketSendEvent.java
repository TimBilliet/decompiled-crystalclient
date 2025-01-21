package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
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
