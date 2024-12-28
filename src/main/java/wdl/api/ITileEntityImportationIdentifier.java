package wdl.api;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

public interface ITileEntityImportationIdentifier extends IWDLMod {
  boolean shouldImportTileEntity(String paramString, BlockPos paramBlockPos, Block paramBlock, NBTTagCompound paramNBTTagCompound, Chunk paramChunk);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\ITileEntityImportationIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */