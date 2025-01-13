package co.crystaldev.client.group.objects;

import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.feature.impl.hud.GroupStatus;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.util.MathHelper;

import java.util.Comparator;

public class PlayerStatusUpdate implements Comparator<PlayerStatusUpdate>, Comparable<PlayerStatusUpdate> {
    public String toString() {
        return "PlayerStatusUpdate(username=" + getUsername() + ", uuid=" + getUuid() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", health=" + getHealth() + ", pots=" + getPots() + ", helmet=" + getHelmet() + ", boots=" + getBoots() + ")";
    }

    private transient String username = null;

    @SerializedName("uuid")
    private final String uuid;

    @SerializedName("x")
    private final int x;

    @SerializedName("y")
    private final int y;

    @SerializedName("z")
    private final int z;

    @SerializedName("health")
    private final int health;

    @SerializedName("pots")
    private final int pots;

    @SerializedName("helmet")
    private final float helmet;

    @SerializedName("boots")
    private final float boots;

    public String getUsername() {
        return this.username;
    }

    public String getUuid() {
        return this.uuid;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getHealth() {
        return this.health;
    }

    public int getPots() {
        return this.pots;
    }

    public float getHelmet() {
        return this.helmet;
    }

    public float getBoots() {
        return this.boots;
    }

    public PlayerStatusUpdate(String uuid, int x, int y, int z, int health, int pots, float helmet, float boots) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.health = health;
        this.pots = pots;
        this.helmet = helmet;
        this.boots = boots;
        updateUsername();
    }

    public void updateUsername() {
        this.username = UsernameCache.getInstance().getUsername(UUIDTypeAdapter.fromString(this.uuid));
    }

    private int calcPriority() {
        int numOfPots = Math.max(this.pots, 30);
        int playerHealth = Math.max(this.health, 15);
        float pot = numOfPots / 30.0F;
        float helm = MathHelper.clamp_float((this.helmet == 0.0F) ? 1.0F : this.helmet, 0.0F, 1.0F);
        float boot = MathHelper.clamp_float((this.boots == 0.0F) ? 1.0F : this.boots, 0.0F, 1.0F);
        float health = playerHealth / 15.0F;
        return (int) Math.floor((pot * 50.0F * ((GroupStatus.getInstance()).showBoots ? boot : helm) * 25.0F * health));
    }

    public int compareTo(PlayerStatusUpdate other) {
        if (other == null)
            return 1;
        return calcPriority() - other.calcPriority();
    }

    public int compare(PlayerStatusUpdate o1, PlayerStatusUpdate o2) {
        return o1.compareTo(o2);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\PlayerStatusUpdate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */