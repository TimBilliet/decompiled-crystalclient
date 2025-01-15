package co.crystaldev.client.group;


import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.GroupMember;
import java.util.HashSet;
import java.util.Set;

import co.crystaldev.client.group.provider.GroupChunkProvider;
import mapwriter.api.MwAPI;

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