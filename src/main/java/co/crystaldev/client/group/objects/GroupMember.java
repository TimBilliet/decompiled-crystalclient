package co.crystaldev.client.group.objects;


import co.crystaldev.client.Client;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.group.objects.enums.Rank;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.BlockPos;

public class GroupMember {
    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("rank")
    private Rank rank;

    private BlockPos pingLocation;

    private PlayerStatusUpdate status;

    public String toString() {
        return "GroupMember(uuid=" + getUuid() + ", rank=" + getRank() + ", pingLocation=" + getPingLocation() + ", status=" + getStatus() + ", lastStatusUpdate=" + getLastStatusUpdate() + ", online=" + isOnline() + ", lastUpdate=" + this.lastUpdate + ")";
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Rank getRank() {
        return this.rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public BlockPos getPingLocation() {
        return this.pingLocation;
    }

    public PlayerStatusUpdate getStatus() {
        return this.status;
    }

    public void setStatus(PlayerStatusUpdate status) {
        this.status = status;
    }

    private long lastStatusUpdate = 0L;

    public long getLastStatusUpdate() {
        return this.lastStatusUpdate;
    }

    public void setLastStatusUpdate(long lastStatusUpdate) {
        this.lastStatusUpdate = lastStatusUpdate;
    }

    private boolean online = false;

    private long lastUpdate = 0L;

    public GroupMember(UUID uuid, Rank rank) {
        this.uuid = uuid;
        this.rank = rank;
    }

    public enum Action {
        PROMOTE, DEMOTE;
    }

    public boolean hasStatus() {
        return (this.status != null);
    }

    public boolean isOnline() {
        if (System.currentTimeMillis() - this.lastUpdate > 5000L) {
            boolean result = false;
            for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                if (player.getGameProfile().getId().equals(this.uuid) && ((NetworkPlayerInfoExt) player).isOnCrystalClient())
                    result = true;
            }
            this.lastUpdate = System.currentTimeMillis();
            return this.online = result;
        }
        return this.online;
    }

    public boolean isAdmin() {
        return (this.rank == Rank.LEADER || this.rank == Rank.ADMIN);
    }

    public void setPingLocation(BlockPos loc) {
        this.pingLocation = loc;
        Client.getInstance().getExecutor().schedule(() -> validatePingLocation(loc), 30L, TimeUnit.SECONDS);
    }

    private void validatePingLocation(BlockPos loc) {
        if (this.pingLocation == loc)
            this.pingLocation = null;
    }

    public GroupMember() {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\GroupMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */