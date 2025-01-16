package co.crystaldev.client.group.objects;

import co.crystaldev.client.group.annotations.Permission;
import co.crystaldev.client.group.objects.enums.Rank;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class Permissions {
    @Permission(label = "Send Status Updates")
    public static final transient int SEND_STATUS_UPDATES = 0;

    @Permission(label = "Adjust Helper")
    public static final transient int SEND_ADJUST_HELPER = 1;

    @Permission(label = "Ping Location")
    public static final transient int SEND_PING_LOCATION = 2;

    @Permission(label = "Focus Players")
    public static final transient int SEND_FOCUS = 3;

    @Permission(label = "Access Group Chat")
    public static final transient int SEND_CHAT = 4;

    @Permission(label = "Kick Members")
    public static final transient int KICK_MEMBERS = 5;

    @Permission(label = "Promote Members")
    public static final transient int PROMOTE_MEMBERS = 6;

    @Permission(label = "Demote Members")
    public static final transient int DEMOTE_MEMBERS = 7;

    @Permission(label = "Manage Pending Members")
    public static final transient int MANAGE_PENDING_MEMBERS = 8;

    @Permission(label = "Create Schematics")
    public static final transient int CREATE_SCHEMATICS = 9;

    @Permission(label = "Remove Schematics")
    public static final transient int REMOVE_SCHEMATICS = 10;

    @Permission(label = "Manage Permissions")
    public static final transient int MANAGE_PERMISSIONS = 11;

    @Permission(label = "Manage Invite Code")
    public static final transient int MANAGE_INVITE_CODE = 12;

    @Permission(label = "Manage Minimap Chunks")
    public static final transient int MANAGE_MINIMAP_CHUNKS = 13;

    @SerializedName("permissions")
    private final Set<Integer> permissions = new HashSet<>();

    public boolean hasPermission(int permission) {
        return this.permissions.contains(permission);
    }

    public boolean setPermission(int permission) {
        if (this.permissions.contains(permission)) {
            this.permissions.remove(permission);
            return false;
        }
        this.permissions.add(permission);
        return true;
    }

    public void setDefault(Rank rank) {
        this.permissions.clear();
        switch (rank) {
            case LEADER:
            case ADMIN:
                setPermission(0);
                setPermission(1);
                setPermission(2);
                setPermission(3);
                setPermission(4);
                setPermission(5);
                setPermission(6);
                setPermission(7);
                setPermission(8);
                setPermission(9);
                setPermission(10);
                setPermission(11);
                setPermission(12);
                return;
            case MODERATOR:
                setPermission(0);
                setPermission(1);
                setPermission(2);
                setPermission(3);
                setPermission(4);
                setPermission(5);
                setPermission(6);
                setPermission(7);
                setPermission(9);
                setPermission(10);
                setPermission(13);
                return;
        }
        setPermission(0);
        setPermission(2);
        setPermission(3);
        setPermission(4);
        setPermission(9);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\Permissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */