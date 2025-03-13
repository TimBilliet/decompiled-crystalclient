package com.github.lunatrius.schematica.api.event;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.timmekeclient.event.Event;
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
                    String.format("Could not replace block type %s, the block type %s already exists in the schematic.", new Object[]{oldName, newName}));
        Short id = this.mappings.get(oldName);
        if (id != null) {
            this.mappings.remove(oldName);
            this.mappings.put(newName, id);
            return true;
        }
        return false;
    }
}
