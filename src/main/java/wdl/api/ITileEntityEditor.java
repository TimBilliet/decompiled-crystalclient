package wdl.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public interface ITileEntityEditor extends IWDLMod {
    boolean shouldEdit(BlockPos paramBlockPos, NBTTagCompound paramNBTTagCompound, TileEntityCreationMode paramTileEntityCreationMode);

    void editTileEntity(BlockPos paramBlockPos, NBTTagCompound paramNBTTagCompound, TileEntityCreationMode paramTileEntityCreationMode);

    public enum TileEntityCreationMode {
        IMPORTED, EXISTING, NEW;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\ITileEntityEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */