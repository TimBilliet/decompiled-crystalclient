package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;

public interface IGuiHooksListener extends IWDLMod {
  boolean onBlockGuiClosed(WorldClient paramWorldClient, BlockPos paramBlockPos, Container paramContainer);
  
  boolean onEntityGuiClosed(WorldClient paramWorldClient, Entity paramEntity, Container paramContainer);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IGuiHooksListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */