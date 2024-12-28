package wdl.api;

import net.minecraft.entity.Entity;

public interface IEntityEditor extends IWDLMod {
  boolean shouldEdit(Entity paramEntity);
  
  void editEntity(Entity paramEntity);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IEntityEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */