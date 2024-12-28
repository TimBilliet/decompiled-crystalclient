package com.github.lunatrius.core.world.chunk;

import net.minecraft.util.BlockPos;

import java.util.Random;

public class ChunkHelper {
  private static final Random RANDOM = new Random();
  
  public static boolean isSlimeChunk(long seed, BlockPos pos) {
    int x = pos.getX() >> 4;
    int z = pos.getZ() >> 4;
    RANDOM.setSeed(seed + (x * x * 4987142) + (x * 5947611) + (z * z * 4392871) + (z * 389711) ^ 0x3AD8025FL);
    return (RANDOM.nextInt(10) == 0);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\world\chunk\ChunkHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */