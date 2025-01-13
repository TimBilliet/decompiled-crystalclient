package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import net.minecraft.nbt.NBTTagCompound;

public class SchematicClassic extends SchematicFormat {
    public ISchematic readFromNBT(NBTTagCompound tagCompound) {
        return null;
    }

    public boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic) {
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\schematic\SchematicClassic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */