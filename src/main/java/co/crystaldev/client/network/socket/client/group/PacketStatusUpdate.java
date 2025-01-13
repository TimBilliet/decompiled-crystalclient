package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.group.objects.PlayerStatusUpdate;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;
import java.util.UUID;

public class PacketStatusUpdate extends Packet {
    private PlayerStatusUpdate statusUpdate;

    public PlayerStatusUpdate getStatusUpdate() {
        return this.statusUpdate;
    }

    public void setStatusUpdate(PlayerStatusUpdate statusUpdate) {
        this.statusUpdate = statusUpdate;
    }

    public PacketStatusUpdate() {
    }

    public PacketStatusUpdate(String uuid, int x, int y, int z, int health, int pots, float helmet, float boots) {
        this.statusUpdate = new PlayerStatusUpdate(uuid, x, y, z, health, pots, helmet, boots);
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(Reference.GSON.toJson(this.statusUpdate));
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.statusUpdate = (PlayerStatusUpdate) Reference.GSON.fromJson(in.readString(), PlayerStatusUpdate.class);
    }

    public void process(INetHandler handler) {
        if (GroupManager.getSelectedGroup() != null) {
            GroupMember mem = GroupManager.getSelectedGroup().getMember(UUID.fromString(this.statusUpdate.getUuid()));
            if (mem != null) {
                mem.setStatus(this.statusUpdate);
                mem.setLastStatusUpdate(System.currentTimeMillis());
            }
        }
    }
}
