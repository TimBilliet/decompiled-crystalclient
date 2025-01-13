package mapwriter.tasks;

import mapwriter.MapWriterMod;
import mapwriter.map.MapTexture;
import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateSurfaceChunksTask extends Task {
    private MwChunk chunk;

    private final RegionManager regionManager;

    private final MapTexture mapTexture;

    private final AtomicBoolean running = new AtomicBoolean();

    private static final Map<Long, UpdateSurfaceChunksTask> chunksUpdating = new HashMap<>();

    public UpdateSurfaceChunksTask(MapWriterMod mapWriterMod, MwChunk chunk) {
        this.mapTexture = mapWriterMod.mapTexture;
        this.regionManager = mapWriterMod.regionManager;
        this.chunk = chunk;
    }

    public void run() {
        this.running.set(true);
        if (this.chunk != null) {
            this.regionManager.updateChunk(this.chunk);
            this.mapTexture.updateArea(this.regionManager, this.chunk.x << 4, this.chunk.z << 4, 16, 16, this.chunk.dimension);
        }
    }

    public void onComplete() {
        Long coords = this.chunk.getCoordIntPair();
        chunksUpdating.remove(coords);
        this.running.set(false);
    }

    public void UpdateChunkData(MwChunk chunk) {
        this.chunk = chunk;
    }

    public boolean CheckForDuplicate() {
        Long coords = ChunkCoordIntPair.chunkXZ2Int(this.chunk.x, this.chunk.z);
        if (!chunksUpdating.containsKey(coords)) {
            chunksUpdating.put(coords, this);
            return false;
        }
        UpdateSurfaceChunksTask task2 = chunksUpdating.get(coords);
        if (!task2.running.get()) {
            task2.UpdateChunkData(this.chunk);
        } else {
            chunksUpdating.put(coords, this);
            return false;
        }
        return true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\UpdateSurfaceChunksTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */