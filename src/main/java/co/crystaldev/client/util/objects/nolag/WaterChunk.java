package co.crystaldev.client.util.objects.nolag;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WaterChunk {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final int x;

    private final int z;

    private final int dimension;

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getDimension() {
        return this.dimension;
    }

    private boolean waterChunk = false;

    private long lastUpdate = -1L;

    public WaterChunk(BlockPos pos) {
        this.x = pos.getX();
        this.z = pos.getZ();
        this.dimension = mc.thePlayer.dimension;
        updateStatus();
    }

    public boolean isWaterChunk() {
        updateStatus();
        return this.waterChunk;
    }

    public double distanceTo(int x, int z) {
        int diffX = this.x - x, diffZ = this.z - z;
        return Math.sqrt((diffX * diffX + diffZ * diffZ));
    }

    private void updateStatus() {
        long currentMs = System.currentTimeMillis();
        if (mc.theWorld == null || currentMs - this.lastUpdate < 30000L)
            return;
        Chunk chunk = mc.theWorld.getChunkProvider().provideChunk(this.x, this.z);
        if (chunk == null) {
            this.waterChunk = false;
            return;
        }
        EXECUTOR.submit(() -> {
            int total = 0;
            int water = 0;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 1; y < mc.theWorld.getHeight(); y += 2) {
                        Block block = chunk.getBlock(x, y, z);
                        boolean liquid = block instanceof net.minecraft.block.BlockLiquid;
                        if (liquid)
                            water++;
                        total++;
                    }
                }
            }
            this.lastUpdate = currentMs;
            this.waterChunk = (water / total > 0.3D);
        });
    }

    public int hashCode() {
        int res = 1;
        res = 31 * res + this.x;
        res = 31 * res + this.z;
        res = 31 * res + this.dimension;
        return res;
    }

    public boolean equals(Object object) {
        if (!(object instanceof WaterChunk))
            return false;
        WaterChunk other = (WaterChunk) object;
        return (other.x == this.x && other.z == this.z && other.dimension == this.dimension);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\nolag\WaterChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */