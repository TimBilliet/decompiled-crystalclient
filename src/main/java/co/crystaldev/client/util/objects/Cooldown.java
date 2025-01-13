package co.crystaldev.client.util.objects;

import net.minecraft.item.ItemStack;

public class Cooldown {
    private final ItemStack itemStack;

    private final long duration;

    private final long startTime;

    public Cooldown(ItemStack itemStack, long duration, long startTime) {
        this.itemStack = itemStack;
        this.duration = duration;
        this.startTime = startTime;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Cooldown))
            return false;
        Cooldown other = (Cooldown) o;
        if (!other.canEqual(this))
            return false;
        if (getDuration() != other.getDuration())
            return false;
        if (getStartTime() != other.getStartTime())
            return false;
        Object this$itemStack = getItemStack(), other$itemStack = other.getItemStack();
        return !((this$itemStack == null) ? (other$itemStack != null) : !this$itemStack.equals(other$itemStack));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Cooldown;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $duration = getDuration();
        result = result * 59 + (int) ($duration >>> 32L ^ $duration);
        long $startTime = getStartTime();
        result = result * 59 + (int) ($startTime >>> 32L ^ $startTime);
        Object $itemStack = getItemStack();
        return result * 59 + (($itemStack == null) ? 43 : $itemStack.hashCode());
    }

    public String toString() {
        return "Cooldown(itemStack=" + getItemStack() + ", duration=" + getDuration() + ", startTime=" + getStartTime() + ")";
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getTimeRemaining() {
        return Math.max(0L, this.duration - System.currentTimeMillis() - this.startTime);
    }

    public boolean isComplete() {
        return (System.currentTimeMillis() - this.startTime > this.duration);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Cooldown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */