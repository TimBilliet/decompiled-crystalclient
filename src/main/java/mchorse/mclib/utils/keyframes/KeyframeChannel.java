package mchorse.mclib.utils.keyframes;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyframeChannel {
    @Expose
    protected final List<Keyframe> keyframes = new ArrayList<>();

    protected Keyframe create(long tick, double value) {
        return new Keyframe(tick, value);
    }

    public boolean isEmpty() {
        return this.keyframes.isEmpty();
    }

    public List<Keyframe> getKeyframes() {
        return this.keyframes;
    }

    public boolean has(int index) {
        return (index >= 0 && index < this.keyframes.size());
    }

    public Keyframe get(int index) {
        return has(index) ? this.keyframes.get(index) : null;
    }

    public void remove(int index) {
        if (index < 0 || index > this.keyframes.size() - 1)
            return;
        Keyframe frame = this.keyframes.remove(index);
        frame.prev.next = frame.next;
        frame.next.prev = frame.prev;
    }

    public double interpolate(float ticks) {
        if (this.keyframes.isEmpty())
            return 0.0D;
        Keyframe prev = this.keyframes.get(0);
        if (ticks < (float) prev.tick)
            return prev.value;
        prev = null;
        for (Keyframe frame : this.keyframes) {
            if (prev != null && ticks >= (float) prev.tick && ticks < (float) frame.tick)
                return prev.interpolate(frame, (ticks - (float) prev.tick) / (float) (frame.tick - prev.tick));
            prev = frame;
        }
        return prev.value;
    }

    public int insert(long tick, double value) {
        Keyframe prev = null;
        if (!this.keyframes.isEmpty()) {
            prev = this.keyframes.get(0);
            if (tick < prev.tick) {
                this.keyframes.add(0, create(tick, value));
                return 0;
            }
        }
        prev = null;
        int index = 0;
        for (Keyframe keyframe : this.keyframes) {
            if (keyframe.tick == tick) {
                keyframe.value = value;
                return index;
            }
            if (prev != null && tick > prev.tick && tick < keyframe.tick)
                break;
            index++;
            prev = keyframe;
        }
        Keyframe frame = create(tick, value);
        this.keyframes.add(index, frame);
        if (this.keyframes.size() > 1) {
            frame.prev = this.keyframes.get(Math.max(index - 1, 0));
            frame.next = this.keyframes.get(Math.min(index + 1, this.keyframes.size() - 1));
        }
        return index;
    }

    public void sort() {
        Collections.sort(this.keyframes, (a, b) -> (int) (a.tick - b.tick));
        if (!this.keyframes.isEmpty()) {
            Keyframe prev = this.keyframes.get(0);
            for (Keyframe frame : this.keyframes) {
                frame.prev = prev;
                prev.next = frame;
                prev = frame;
            }
            prev.next = prev;
        }
    }

    public void copy(KeyframeChannel channel) {
        this.keyframes.clear();
        for (Keyframe frame : channel.keyframes)
            this.keyframes.add(frame.copy());
        sort();
    }

    public void fromByteBuf(ByteBuf buffer) {
        this.keyframes.clear();
        for (int i = 0, c = buffer.readInt(); i < c; i++) {
            Keyframe frame = new Keyframe(buffer.readLong(), buffer.readDouble());
            frame.fromByteBuf(buffer);
            this.keyframes.add(frame);
        }
        sort();
    }

    public void toByteBuf(ByteBuf buffer) {
        buffer.writeInt(this.keyframes.size());
        for (Keyframe frame : this.keyframes)
            frame.toByteBuf(buffer);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\keyframes\KeyframeChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */