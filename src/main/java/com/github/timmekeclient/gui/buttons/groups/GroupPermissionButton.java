package com.github.timmekeclient.gui.buttons.groups;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.group.objects.enums.Rank;
import com.github.timmekeclient.gui.buttons.settings.ToggleButton;
import com.github.timmekeclient.network.Packet;
import com.github.timmekeclient.network.socket.client.group.PacketGroupPermissionUpdate;

public class GroupPermissionButton extends ToggleButton {
    private Rank rank;

    private final int permission;

    public Rank getRank() {
        return this.rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public int getPermission() {
        return this.permission;
    }

    public GroupPermissionButton(Rank rank, int permission, int x, int y, int width, int height, String displayText) {
        super(-1, x, y, width, height, displayText, (GroupManager.getSelectedGroup() != null &&
                GroupManager.getSelectedGroup().hasPermission(rank, permission)));
        this.rank = rank;
        this.permission = permission;
        setValue(Boolean.valueOf(GroupManager.getSelectedGroup().hasPermission(11)));
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        super.drawButton(mouseX, mouseY, hovered);
        Group group = GroupManager.getSelectedGroup();
        setValue(Boolean.valueOf((group != null && group.hasPermission(this.rank, this.permission))));
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        if (isEnabled()) {
            super.onInteract(mouseX, mouseY, mouseButton);
            PacketGroupPermissionUpdate packet = new PacketGroupPermissionUpdate(this.rank, this.permission);
            Client.sendPacket((Packet) packet);
        }
    }
}
