package co.crystaldev.client.util;

import co.crystaldev.client.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class LocationUtils {
    public static boolean isBlockInSameChunk(BlockPos posIn, Entity entityIn) {
        int posChunkX = posIn.getX() >> 4;
        int posChunkZ = posIn.getZ() >> 4;
        return (posChunkX == entityIn.chunkCoordX && posChunkZ == entityIn.chunkCoordZ);
    }

    public static Vec3 getViewPosition() {
        float partialTicks = (Client.getTimer()).renderPartialTicks;
        return Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\LocationUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */