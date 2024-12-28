package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

public interface IWritableLocation {
  void fromNbt(NBTBase paramNBTBase) throws Exception;
  
  void fromJson(JsonElement paramJsonElement) throws Exception;
  
  NBTBase writeNbt();
  
  JsonElement writeJson();
  
  ResourceLocation clone();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\IWritableLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */