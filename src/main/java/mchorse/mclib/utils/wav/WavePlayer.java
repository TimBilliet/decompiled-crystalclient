package mchorse.mclib.utils.wav;

import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;

public class WavePlayer {
    private int buffer = -1;

    private int source = -1;

    private float duration;

    public WavePlayer initialize(Wave wave) {
        this.buffer = AL10.alGenBuffers();
        ByteBuffer buffer = GLAllocation.createDirectByteBuffer(wave.data.length);
        buffer.put(wave.data);
        buffer.flip();
        AL10.alBufferData(this.buffer, wave.getALFormat(), buffer, wave.sampleRate);
        this.duration = wave.getDuration();
        this.source = AL10.alGenSources();
        AL10.alSourcei(this.source, 4105, this.buffer);
        AL10.alSourcei(this.source, 514, 1);
        return this;
    }

    public void delete() {
        AL10.alDeleteBuffers(this.buffer);
        AL10.alDeleteSources(this.source);
        this.buffer = -1;
        this.source = -1;
    }

    public void play() {
        AL10.alSourcePlay(this.source);
    }

    public void pause() {
        AL10.alSourcePause(this.source);
    }

    public void stop() {
        AL10.alSourceStop(this.source);
    }

    public int getSourceState() {
        return AL10.alGetSourcei(this.source, 4112);
    }

    public boolean isPlaying() {
        return (getSourceState() == 4114);
    }

    public boolean isPaused() {
        return (getSourceState() == 4115);
    }

    public boolean isStopped() {
        int state = getSourceState();
        return (state == 4116 || state == 4113);
    }

    public float getPlaybackPosition() {
        return AL10.alGetSourcef(this.source, 4132);
    }

    public void setPlaybackPosition(float seconds) {
        seconds = MathUtils.clamp(seconds, 0.0F, this.duration);
        AL10.alSourcef(this.source, 4132, seconds);
    }

    public int getBuffer() {
        return this.buffer;
    }

    public int getSource() {
        return this.source;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\wav\WavePlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */