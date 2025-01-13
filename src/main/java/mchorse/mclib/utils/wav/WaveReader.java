package mchorse.mclib.utils.wav;

import mchorse.mclib.utils.binary.BinaryChunk;
import mchorse.mclib.utils.binary.BinaryReader;

import java.io.InputStream;

public class WaveReader extends BinaryReader {
    public Wave read(InputStream stream) throws Exception {
        try {
            BinaryChunk main = readChunk(stream);
            if (!main.id.equals("RIFF"))
                throw new Exception("Given file is not 'RIFF'! It's '" + main.id + "' instead...");
            String format = readFourString(stream);
            if (!format.equals("WAVE"))
                throw new Exception("Given RIFF file is not a 'WAVE' file! It's '" + format + "' instead...");
            int audioFormat = -1;
            int numChannels = -1;
            int sampleRate = -1;
            int byteRate = -1;
            int blockAlign = -1;
            int bitsPerSample = -1;
            byte[] data = null;
            int read = 0;
            while (read < 2) {
                BinaryChunk chunk = readChunk(stream);
                if (chunk.id.equals("fmt ")) {
                    audioFormat = readShort(stream);
                    numChannels = readShort(stream);
                    sampleRate = readInt(stream);
                    byteRate = readInt(stream);
                    blockAlign = readShort(stream);
                    bitsPerSample = readShort(stream);
                    if (chunk.size > 16)
                        stream.skip((chunk.size - 16));
                    read++;
                    continue;
                }
                if (chunk.id.equals("data")) {
                    data = new byte[chunk.size];
                    stream.read(data);
                    read++;
                    continue;
                }
                skip(stream, chunk.size);
            }
            stream.close();
            return new Wave(audioFormat, numChannels, sampleRate, byteRate, blockAlign, bitsPerSample, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BinaryChunk readChunk(InputStream stream) throws Exception {
        String id = readFourString(stream);
        int size = readInt(stream);
        return new BinaryChunk(id, size);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\wav\WaveReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */