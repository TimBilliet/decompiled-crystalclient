package wdl.api;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;

public interface IPlayerInfoEditor extends IWDLMod {
  void editPlayerInfo(EntityPlayerSP paramEntityPlayerSP, SaveHandler paramSaveHandler, NBTTagCompound paramNBTTagCompound);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IPlayerInfoEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */