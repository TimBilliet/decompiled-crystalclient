package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.gui.screens.groups.ScreenGroups;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;

import java.io.IOException;

public class PacketGroupUpdate extends Packet {
    private Group selectedGroup;

    private String id;

    public PacketGroupUpdate() {
    }

    public PacketGroupUpdate(String id) {
        this.id = (id == null) ? "null" : id;
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.id);
    }

    public void read(ByteBufWrapper in) throws IOException {
        this.selectedGroup = (Group) Reference.GSON.fromJson(in.readString(), Group.class);
    }

    public void process(INetHandler handler) {
        GroupManager.setSelectedGroup(this.selectedGroup);
        ScreenGroups.updateGroup();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupUpdate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */