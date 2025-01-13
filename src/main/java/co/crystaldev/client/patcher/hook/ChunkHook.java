package co.crystaldev.client.patcher.hook;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkHook {
    public static IBlockState getBlockState(Chunk chunk, BlockPos pos) {
        int y = pos.getY();
        if (y >= 0 && y >> 4 < (chunk.getBlockStorageArray()).length) {
            ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y >> 4];
            if (storage != null)
                return storage.get(pos.getX() & 0xF, y & 0xF, pos.getZ() & 0xF);
        }
        return Blocks.air.getDefaultState();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\hook\ChunkHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */