package co.crystaldev.client.group.objects;

import co.crystaldev.client.group.objects.enums.HighlightedChunk;
import com.google.gson.annotations.SerializedName;

public class ChunkHighlight {
    @SerializedName("x")
    private final int x;

    @SerializedName("z")
    private final int z;

    @SerializedName("type")
    private final HighlightedChunk type;

    @SerializedName("data")
    private final String data;

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public HighlightedChunk getType() {
        return this.type;
    }

    public String getData() {
        return this.data;
    }

    public ChunkHighlight(int x, int z, HighlightedChunk type, String data) {
        this.x = x;
        this.z = z;
        this.type = type;
        this.data = data;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\ChunkHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */