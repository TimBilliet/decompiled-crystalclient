package mchorse.mclib.utils.wav;

import net.minecraft.client.renderer.GLAllocation;

import java.nio.ByteBuffer;

public class Wave {
    public int audioFormat;

    public int numChannels;

    public int sampleRate;

    public int byteRate;

    public int blockAlign;

    public int bitsPerSample;

    public byte[] data;

    public Wave(int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample, byte[] data) {
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.byteRate = byteRate;
        this.blockAlign = blockAlign;
        this.bitsPerSample = bitsPerSample;
        this.data = data;
    }

    public int getBytesPerSample() {
        return this.bitsPerSample / 8;
    }

    public float getDuration() {
        return (this.data.length / this.numChannels / getBytesPerSample()) / this.sampleRate;
    }

    public int getALFormat() {
        int bytes = getBytesPerSample();
        if (bytes == 1) {
            if (this.numChannels == 2)
                return 4354;
            if (this.numChannels == 1)
                return 4352;
        } else if (bytes == 2) {
            if (this.numChannels == 2)
                return 4355;
            if (this.numChannels == 1)
                return 4353;
        }
        throw new IllegalStateException("Current WAV file has unusual configuration... channels: " + this.numChannels + ", BPS: " + bytes);
    }

    public int getScanRegion(float pixelsPerSecond) {
        return (int) (this.sampleRate / pixelsPerSecond) * getBytesPerSample() * this.numChannels;
    }

    public Wave convertTo16() {
        int bytes = 2;
        int c = this.data.length / this.numChannels / getBytesPerSample();
        int byteRate = c * this.numChannels * 2;
        byte[] data = new byte[byteRate];
        boolean isFloat = (getBytesPerSample() == 4);
        Wave wave = new Wave(this.audioFormat, this.numChannels, this.sampleRate, byteRate, 2 * this.numChannels, 16, data);
        ByteBuffer sample = GLAllocation.createDirectByteBuffer(4);
        ByteBuffer dataBuffer = GLAllocation.createDirectByteBuffer(data.length);
        for (int i = 0; i < c * this.numChannels; i++) {
            sample.clear();
            for (int j = 0; j < getBytesPerSample(); j++)
                sample.put(this.data[i * getBytesPerSample() + j]);
            if (isFloat) {
                sample.flip();
                dataBuffer.putShort((short) (int) (sample.getFloat() * 65535.0F / 2.0F));
            } else {
                sample.put((byte) 0);
                sample.flip();
                dataBuffer.putShort((short) (int) (sample.getInt() / 8388607.5F * 32767.5F));
            }
        }
        dataBuffer.flip();
        dataBuffer.get(data);
        return wave;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\wav\Wave.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */