package mchorse.mclib.utils;

public class Timer {
    public boolean enabled;

    public long time;

    public long duration;

    public Timer(long duration) {
        this.duration = duration;
    }

    public long getRemaining() {
        return this.time - System.currentTimeMillis();
    }

    public void mark() {
        mark(this.duration);
    }

    public void mark(long duration) {
        this.enabled = true;
        this.time = System.currentTimeMillis() + duration;
    }

    public void reset() {
        this.enabled = false;
    }

    public boolean checkReset() {
        boolean enabled = check();
        if (enabled)
            reset();
        return enabled;
    }

    public boolean check() {
        return (this.enabled && isTime());
    }

    public boolean isTime() {
        return (System.currentTimeMillis() >= this.time);
    }

    public boolean checkRepeat() {
        if (!this.enabled)
            mark();
        return checkReset();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */