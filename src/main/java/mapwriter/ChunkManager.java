package mapwriter;

import co.crystaldev.client.feature.impl.hud.MapWriter;
import mapwriter.config.Config;
import mapwriter.region.MwChunk;
import mapwriter.tasks.SaveChunkTask;
import mapwriter.tasks.Task;
import mapwriter.tasks.UpdateSurfaceChunksTask;
import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Arrays;
import java.util.Map;

public class ChunkManager {
    public MapWriterMod mapWriterMod;

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean closed = false;

    private final CircularHashMap<Chunk, Integer> chunkMap = new CircularHashMap<>();

    private static final int VISIBLE_FLAG = 1;

    private static final int VIEWED_FLAG = 2;

    private long lastUpdateTime = 0L;

    private int lastChunkX;

    private int lastChunkZ;

    public ChunkManager(MapWriterMod mapWriterMod) {
        this.mapWriterMod = mapWriterMod;
    }

    public synchronized void close() {
        this.closed = true;
        saveChunks();
        this.chunkMap.clear();
    }

    public static MwChunk copyToMwChunk(Chunk chunk) {
        Map<BlockPos, TileEntity> tileEntityMap = Utils.checkedMapByCopy(chunk.getTileEntityMap(), BlockPos.class, TileEntity.class, false);
        char[][] dataArray = new char[16][];
        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
        if (storageArrays != null)
            for (ExtendedBlockStorage storage : storageArrays) {
                if (storage != null) {
                    int y = storage.getYLocation() >> 4 & 0xF;
                    dataArray[y] = storage.getData();
                }
            }
        return new MwChunk(chunk.xPosition, chunk.zPosition,

                (chunk.getWorld()).provider.getDimensionId(), dataArray,

                Arrays.copyOf(chunk.getBiomeArray(), (chunk.getBiomeArray()).length), tileEntityMap);
    }

    public synchronized void addChunk(Chunk chunk) {
        if (!this.closed && chunk != null)
            this.chunkMap.put(chunk, 0);
    }

    public synchronized void removeChunk(Chunk chunk) {
        if (!this.closed && chunk != null) {
            if (!this.chunkMap.containsKey(chunk))
                return;
            int flags = this.chunkMap.get(chunk);
            if ((flags & 0x2) != 0)
                addSaveChunkTask(chunk);
            this.chunkMap.remove(chunk);
        }
    }

    public synchronized void saveChunks() {
        for (Map.Entry<Chunk, Integer> entry : this.chunkMap.entrySet()) {
            int flags = entry.getValue();
            if ((flags & 0x2) != 0)
                addSaveChunkTask(entry.getKey());
        }
    }

    public void updateSurfaceChunks() {
        int chunksToUpdate = Math.min(this.chunkMap.size(), (MapWriter.getInstance()).chunksPerTick);
        MwChunk[] chunkArray = new MwChunk[chunksToUpdate];
        for (int i = 0; i < chunksToUpdate; i++) {
            Map.Entry<Chunk, Integer> entry = this.chunkMap.getNextEntry();
            if (entry != null) {
                Chunk chunk = entry.getKey();
                int flags = entry.getValue();
                if (Utils.distToChunkSq(this.mapWriterMod.playerXInt, this.mapWriterMod.playerZInt, chunk) <= Config.maxChunkSaveDistSq) {
                    flags |= 0x3;
                } else {
                    flags &= 0xFFFFFFFE;
                }
                entry.setValue(flags);
                if ((flags & 0x1) != 0) {
                    chunkArray[i] = copyToMwChunk(chunk);
                    this.mapWriterMod.executor.addTask((Task) new UpdateSurfaceChunksTask(this.mapWriterMod, chunkArray[i]));
                } else {
                    chunkArray[i] = null;
                }
            }
        }
    }

    public void onTick() {
        if (!this.closed) {
            long currentMs = System.currentTimeMillis();
            boolean flag = (currentMs - this.lastUpdateTime > 2000L);
            if (this.mc.thePlayer != null) {
                int chunkX = this.mc.thePlayer.chunkCoordX;
                int chunkZ = this.mc.thePlayer.chunkCoordZ;
                flag = (flag || chunkX != this.lastChunkX || chunkZ != this.lastChunkZ);
                if (flag) {
                    this.lastChunkX = chunkX;
                    this.lastChunkZ = chunkZ;
                }
            }
            if (flag) {
                this.lastUpdateTime = currentMs;
                updateSurfaceChunks();
            }
        }
    }

    private void addSaveChunkTask(Chunk chunk) {
        if ((this.mc.isSingleplayer() || !this.mc.isSingleplayer()) &&
                !chunk.isEmpty())
            this.mapWriterMod.executor.addTask((Task) new SaveChunkTask(copyToMwChunk(chunk), this.mapWriterMod.regionManager));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\ChunkManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */