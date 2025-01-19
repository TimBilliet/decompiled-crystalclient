package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketGroupInvitationAction extends Packet {
    private String message;

    private String inviteCode;

    private Action action;

    public PacketGroupInvitationAction() {
    }

    public PacketGroupInvitationAction(String inviteCode, Action action) {
        this.inviteCode = inviteCode;
        this.action = action;
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("invite", this.inviteCode);
        obj.addProperty("action", this.action.toString());
        out.writeString(Reference.GSON.toJson((JsonElement) obj));
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.message = obj.get("message").getAsString();
        this.action = Action.fromString(obj.get("action").getAsString());
    }

    public void process(INetHandler handler) {
        if (this.action == Action.REQUEST_JOIN) {
            if(message.contains("invalid_invitation_code")) {
                NotificationHandler.addNotification("Invalid invitation code");
            } else if(message.contains("member_already_in_group")) {
                NotificationHandler.addNotification("You are already a member of this group!");
            } else if(message.contains("invite_sent_successfully")) {
                NotificationHandler.addNotification("Invite sent successfully");
            } else if(message.contains("member_already_pending")) {
                NotificationHandler.addNotification("Your invitation is already pending!");
            } else {
                NotificationHandler.addNotification(this.message);
            }
        } else if (GroupManager.getSelectedGroup() != null) {
            GroupManager.getSelectedGroup().setInviteCode(this.message);
        }
    }

    public enum Action {
        REQUEST_JOIN("REQUEST_JOIN"),
        REQUEST_RESET("REQUEST_RESET");

        Action(String serializationString) {
            this.serializationString = serializationString;
        }

        private final String serializationString;

        public String getSerializationString() {
            return this.serializationString;
        }

        public String toString() {
            return this.serializationString;
        }

        public static Action fromString(String name) {
            for (Action action : values()) {
                if (action.getSerializationString().equalsIgnoreCase(name))
                    return action;
            }
            return null;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupInvitationAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */