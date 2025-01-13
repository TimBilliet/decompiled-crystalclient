package co.crystaldev.client.util.objects;

import co.crystaldev.client.Client;
import co.crystaldev.client.util.ColorObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

import java.util.Objects;

public class Waypoint {
    @SerializedName("server")
    private String server;

    @SerializedName("name")
    private String name;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Waypoint))
            return false;
        Waypoint other = (Waypoint) o;
        if (!other.canEqual(this))
            return false;
        if (isVisible() != other.isVisible())
            return false;
        if (getDuration() != other.getDuration())
            return false;
        if (isCanBeDeleted() != other.isCanBeDeleted())
            return false;
        Object this$server = getServer(), other$server = other.getServer();
        if ((this$server == null) ? (other$server != null) : !this$server.equals(other$server))
            return false;
        Object this$name = getName(), other$name = other.getName();
        if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
            return false;
        Object this$world = getWorld(), other$world = other.getWorld();
        if ((this$world == null) ? (other$world != null) : !this$world.equals(other$world))
            return false;
        Object this$pos = getPos(), other$pos = other.getPos();
        if ((this$pos == null) ? (other$pos != null) : !this$pos.equals(other$pos))
            return false;
        Object this$color = getColor(), other$color = other.getColor();
        return !((this$color == null) ? (other$color != null) : !this$color.equals(other$color));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Waypoint;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (isVisible() ? 79 : 97);
        long $duration = getDuration();
        result = result * 59 + (int) ($duration >>> 32L ^ $duration);
        result = result * 59 + (isCanBeDeleted() ? 79 : 97);
        Object $server = getServer();
        result = result * 59 + (($server == null) ? 43 : $server.hashCode());
        Object $name = getName();
        result = result * 59 + (($name == null) ? 43 : $name.hashCode());
        Object $world = getWorld();
        result = result * 59 + (($world == null) ? 43 : $world.hashCode());
        Object $pos = getPos();
        result = result * 59 + (($pos == null) ? 43 : $pos.hashCode());
        Object $color = getColor();
        return result * 59 + (($color == null) ? 43 : $color.hashCode());
    }

    public String getServer() {
        return this.server;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("world")
    private String world = "unknown_world";

    @SerializedName("pos")
    private BlockPos pos;

    public String getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @SerializedName("visible")
    private boolean visible = true;

    @SerializedName("color")
    private ColorObject color;

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ColorObject getColor() {
        return this.color;
    }

    public void setColor(ColorObject color) {
        this.color = color;
    }

    @SerializedName("duration")
    private long duration = -1L;

    public long getDuration() {
        return this.duration;
    }

    @SerializedName("can_be_deleted")
    private boolean canBeDeleted = true;

    public boolean isCanBeDeleted() {
        return this.canBeDeleted;
    }

    private transient boolean serverSided = false;

    public boolean isServerSided() {
        return this.serverSided;
    }

    private final transient long createdAt = System.currentTimeMillis();

    public Waypoint(String name, String server, BlockPos pos) {
        this(name, server, pos, new ColorObject(255, 255, 255, 180));
    }

    public Waypoint(String name, String server, BlockPos pos, ColorObject color) {
        this.name = name;
        this.server = server;
        this.pos = pos;
        this.color = color;
    }

    public Waypoint setCanBeDeleted(boolean canBeDeleted) {
        this.canBeDeleted = canBeDeleted;
        return this;
    }

    public Waypoint setServerSided(boolean serverSided) {
        this.serverSided = serverSided;
        return this;
    }

    public Waypoint setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public Waypoint setWorld(String world) {
        this.world = world;
        return this;
    }

    public boolean isSameWorld() {
        return ("unknown_world".equals(this.world) || Client.getCurrentWorldName().equalsIgnoreCase(this.world));
    }

    public boolean isExpired() {
        if (this.duration == -1L)
            return false;
        return (System.currentTimeMillis() - this.createdAt >= this.duration);
    }

    public double distanceTo(Vec3i vec3) {
        return Math.sqrt(this.pos.distanceSq(vec3));
    }

    public boolean isVisible() {
        return (isSameServer() && this.visible);
    }

    public boolean isSameServer() {
        return Objects.equals(this.server, Client.formatConnectedServerIp());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Waypoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */