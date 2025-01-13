package com.github.lunatrius.schematica.nbt;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class NBTConversionException extends Exception {
    public NBTConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTConversionException(TileEntity tileEntity, Throwable cause) {
        super(String.valueOf(tileEntity), cause);
    }

    public NBTConversionException(Entity entity, Throwable cause) {
        super(String.valueOf(entity), cause);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\nbt\NBTConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */