package com.github.lunatrius.schematica.api.event;

import co.crystaldev.client.event.Event;
import com.github.lunatrius.schematica.api.ISchematic;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class PreSchematicSaveEvent extends Event {
  private final Map<String, Short> mappings;
  
  public final ISchematic schematic;
  
  public final NBTTagCompound extendedMetadata;
  
  @Deprecated
  public PreSchematicSaveEvent(Map<String, Short> mappings) {
    this(null, mappings);
  }
  
  public PreSchematicSaveEvent(ISchematic schematic, Map<String, Short> mappings) {
    this.schematic = schematic;
    this.mappings = mappings;
    this.extendedMetadata = new NBTTagCompound();
  }
  
  public boolean replaceMapping(String oldName, String newName) throws DuplicateMappingException {
    if (this.mappings.containsKey(newName))
      throw new DuplicateMappingException(
          String.format("Could not replace block type %s, the block type %s already exists in the schematic.", new Object[] { oldName, newName })); 
    Short id = this.mappings.get(oldName);
    if (id != null) {
      this.mappings.remove(oldName);
      this.mappings.put(newName, id);
      return true;
    } 
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\api\event\PreSchematicSaveEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */