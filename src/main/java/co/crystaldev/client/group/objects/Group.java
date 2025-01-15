package co.crystaldev.client.group.objects;


import co.crystaldev.client.Client;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.group.objects.enums.HighlightedChunk;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.group.provider.GroupChunkProvider;
import co.crystaldev.client.gui.screens.groups.ScreenGroups;
import co.crystaldev.client.gui.screens.groups.SectionSchematics;
import com.google.gson.annotations.SerializedName;
import mapwriter.api.MwAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Group {
    @SerializedName("id")
    private final String id;

    @SerializedName("name")
    private final String name;

    @SerializedName("inviteCode")
    private String inviteCode;

    @SerializedName("schematics")
    private final ArrayList<GroupSchematic> schematics;

    @SerializedName("members")
    private final ArrayList<GroupMember> members;

    @SerializedName("pendingMembers")
    private final ArrayList<GroupMember> pendingMembers;

    @SerializedName("rankPermissions")
    private final HashMap<Rank, Permissions> rankPermissions;

    @SerializedName("highlightedChunks")
    private final HashMap<String, ChunkHighlightGrid> highlightedChunks;

    private transient UUID focusedId;

    public String toString() {
        return "Group(id=" + getId() + ", name=" + getName() + ", inviteCode=" + getInviteCode() + ", schematics=" + getSchematics() + ", members=" + getMembers() + ", pendingMembers=" + getPendingMembers() + ", rankPermissions=" + getRankPermissions() + ", highlightedChunks=" + getHighlightedChunks() + ", focusedId=" + getFocusedId() + ")";
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getInviteCode() {
        return this.inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public ArrayList<GroupSchematic> getSchematics() {
        return this.schematics;
    }

    public ArrayList<GroupMember> getMembers() {
        return this.members;
    }

    public ArrayList<GroupMember> getPendingMembers() {
        return this.pendingMembers;
    }

    public HashMap<Rank, Permissions> getRankPermissions() {
        return this.rankPermissions;
    }

    public HashMap<String, ChunkHighlightGrid> getHighlightedChunks() {
        return this.highlightedChunks;
    }

    public UUID getFocusedId() {
        return this.focusedId;
    }

    public void setFocusedId(UUID focusedId) {
        this.focusedId = focusedId;
    }

    public Group(String id, String name, ArrayList<GroupMember> members, ArrayList<GroupMember> pendingMembers, ArrayList<GroupSchematic> schematics, HashMap<Rank, Permissions> rankPermissions, HashMap<String, ChunkHighlightGrid> highlightedChunks, String inviteCode) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.pendingMembers = pendingMembers;
        this.schematics = schematics;
        this.rankPermissions = rankPermissions;
        this.highlightedChunks = highlightedChunks;
        this.inviteCode = inviteCode;
    }

    public synchronized GroupMember getMember(UUID uuid) {
        for (GroupMember member : this.members) {
            if (member.getUuid().equals(uuid))
                return member;
        }
        return null;
    }

    public synchronized GroupMember getMember(String name) {
        for (GroupMember member : this.members) {
            if (UsernameCache.getInstance().getUsername(member.getUuid()).equals(name))
                return member;
        }
        return null;
    }

    public synchronized void addSchematic(GroupSchematic schematic) {
        this.schematics.add(schematic);
        ScreenGroups.updateSection(SectionSchematics.class);
    }

    public synchronized void removeSchematic(GroupSchematic schematic) {
        this.schematics.removeIf(s -> (s.getId().equals(schematic.getId()) && s.getDir().equals(schematic.getDir())));
        ScreenGroups.updateSection(SectionSchematics.class);
    }

    public synchronized void addPendingMember(UUID id) {
        this.pendingMembers.add(new GroupMember(id, Rank.MEMBER));
        ScreenGroups.updateMembers();
    }

    public synchronized void addMember(UUID id) {
        this.members.add(new GroupMember(id, Rank.MEMBER));
        this.pendingMembers.removeIf(m -> m.getUuid().equals(id));
        ScreenGroups.updateMembers();
    }

    public synchronized void removeMember(UUID id) {
        this.members.removeIf(m -> m.getUuid().equals(id));
        this.pendingMembers.removeIf(m -> m.getUuid().equals(id));
        ScreenGroups.updateMembers();
    }

    public Rank getRank(UUID uuid) {
        GroupMember member = getMember(uuid);
        if (member != null)
            return member.getRank();
        return null;
    }

    public synchronized boolean compareRanks(UUID member, UUID compared) {
        Rank memberRank = getRank(member);
        Rank comparedRank = getRank(compared);
        if (memberRank == null)
            return false;
        if (comparedRank == null)
            return true;
        return (memberRank.ordinal() < comparedRank.ordinal());
    }

    public synchronized boolean canRankBeUpdated(UUID updater, Rank newRank) {
        Rank memberRank = getRank(updater);
        if (newRank == null)
            return false;
        return (memberRank.ordinal() < newRank.ordinal());
    }

    public synchronized boolean setPermission(Rank rank, int permission) {
        Permissions permissions = this.rankPermissions.get(rank);
        if (permissions != null)
            return permissions.setPermission(permission);
        return false;
    }

    public synchronized boolean hasPermission(Rank rank, int permission) {
        if (rank == Rank.LEADER)
            return true;
        Permissions perms = this.rankPermissions.get(rank);
        return (perms != null && perms.hasPermission(permission));
    }

    public synchronized boolean hasPermission(int permission) {
        Rank rank = getMember(Client.getUniqueID()).getRank();
        if (rank == Rank.LEADER)
            return true;
        Permissions perms = this.rankPermissions.get(rank);
        return (perms != null && perms.hasPermission(permission));
    }

    public synchronized void highlightChunk(String server, ChunkHighlight highlight) {
        if (server == null)
            return;
        ChunkHighlightGrid grid = this.highlightedChunks.computeIfAbsent(server, s -> new ChunkHighlightGrid());
        grid.highlightChunk(highlight);
        grid.sort(Comparator.comparing(c -> (c.getType() == HighlightedChunk.TEXT)));
        (MwAPI.getDataProvider(GroupChunkProvider.class)).setAwaitingUpdate(true);
    }

    public synchronized void removeHighlight(String server, int x, int z) {
        if (server == null)
            return;
        for (Map.Entry<String, ChunkHighlightGrid> entry : getHighlightedChunks().entrySet()) {
            if (server.endsWith(entry.getKey())) {
                (entry.getValue()).removeChunk(x, z);
                (MwAPI.getDataProvider(GroupChunkProvider.class)).setAwaitingUpdate(true);
                break;
            }
        }
    }

    public synchronized void clearHighlightedChunks(String server) {
        if (server == null)
            return;
        for (Map.Entry<String, ChunkHighlightGrid> entry : getHighlightedChunks().entrySet()) {
            if (server.endsWith(entry.getKey())) {
                this.highlightedChunks.remove(entry.getKey());
                (MwAPI.getDataProvider(GroupChunkProvider.class)).setAwaitingUpdate(true);
                break;
            }
        }
    }

    public int getOnlineMembers() {
        int count = 0;
        for (GroupMember member : this.members) {
            if (member.isOnline())
                count++;
        }
        return count;
    }

    public int getMemberCount() {
        return this.members.size();
    }
}