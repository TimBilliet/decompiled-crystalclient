package com.github.timmekeclient.group;


import com.github.timmekeclient.cache.UsernameCache;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.group.objects.GroupMember;
import com.github.timmekeclient.group.provider.GroupChunkProvider;
import mapwriter.api.MwAPI;

import java.util.HashSet;
import java.util.Set;

public class GroupManager {
    private static Group selectedGroup;

    public static Group getSelectedGroup() {
        return selectedGroup;
    }

    public static Set<Group> getGroups() {
        return groups;
    }

    private static final Set<Group> groups = new HashSet<>();

    public static void add(Group group) {
        groups.add(group);
    }

    public static void setSelectedGroup(Group group) {
        selectedGroup = group;
        if (group != null)
            for (GroupMember member : group.getMembers())
                UsernameCache.getInstance().getUsername(member.getUuid());
        (MwAPI.getDataProvider(GroupChunkProvider.class)).setAwaitingUpdate(true);
    }

    public static boolean isInGroup() {
        return (selectedGroup != null);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\GroupManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */