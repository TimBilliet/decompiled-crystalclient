package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;

import java.io.IOException;
import java.util.UUID;

public class PacketPendingGroupMemberAction extends Packet {
    private UUID uuid;

    private Action action;

    public PacketPendingGroupMemberAction() {
    }

    public PacketPendingGroupMemberAction(UUID uuid, Action action) {
        this.uuid = uuid;
        this.action = action;
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.uuid);
        out.writeString(this.action.toString());
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.uuid = in.readUUID();
        this.action = Action.fromString(in.readString());
    }

    public void process(INetHandler handler) {
        Group sel = GroupManager.getSelectedGroup();
        if (sel != null && sel.hasPermission(8) &&
                this.action == Action.ADD)
            sel.addPendingMember(this.uuid);
    }

    public enum Action {
        ACCEPT("ACCEPT"),
        DENY("DENY"),
        ADD("ADD");

        Action(String serializationString) {
            this.serializationString = serializationString;
        }

        private final String serializationString;

        public String toString() {
            return this.serializationString;
        }

        public static Action fromString(String name) {
            for (Action action : values()) {
                if (action.toString().equalsIgnoreCase(name))
                    return action;
            }
            return null;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketPendingGroupMemberAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */